/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.iot.pi4j.component;

import java.time.Duration;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.component.HumiditySensor;
import org.springframework.cloud.iot.component.TemperatureSensor;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;
import org.springframework.cloud.iot.support.SensorValue;
import org.springframework.util.Assert;

import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.wiringpi.Gpio;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@code DHT11} {@link HumiditySensor} implementation.
 *
 * @author Janne Valkealahti
 */
public class Pi4jDHT11HumiditySensor extends LifecycleObjectSupport implements HumiditySensor, TemperatureSensor {

	private final Logger log = LoggerFactory.getLogger(Pi4jDHT11HumiditySensor.class);
	private final GpioPinDigitalMultipurpose multi;
	private volatile long lastQueryTime;
	private DataHolder lastData;
	private int queryCycles = 85;
	private SensorValue<DataHolder> sensorValue;
	private final Duration duration;

	public Pi4jDHT11HumiditySensor(GpioPinDigitalMultipurpose multi) {
		Assert.notNull(multi, "GpioPinDigitalMultipurpose must be set");
		this.multi = multi;
		this.duration = Duration.ofSeconds(2);
	}

	@Override
	public String getName() {
		return "DHT11";
	}

	@Override
	public double getHumidity() {
		DataHolder data = queryData();
		return data.getHumidity();
	}

	@Override
	public Flux<Double> humidityAsFlux() {
		return sensorValue.asFlux().map(m -> (double)m.getHumidity());
	}

	@Override
	public Mono<Double> humidityAsMono() {
		return sensorValue.asMono().map(m -> (double)m.getHumidity());
	}

	@Override
	public double getTemperature() {
		DataHolder data = queryData();
		return data.getTemperature();
	}

	@Override
	public Flux<Double> temperatureAsFlux() {
		return sensorValue.asFlux().map(m -> (double)m.getTemperature());
	}

	@Override
	public Mono<Double> temperatureAsMono() {
		return sensorValue.asMono().map(m -> (double)m.getTemperature());
	}

	@Override
	protected void onInit() throws Exception {
		this.sensorValue = new SensorValue<>(new Callable<DataHolder>() {

			@Override
			public DataHolder call() throws Exception {
				return queryData();
			}
		}, duration);
		this.sensorValue.afterPropertiesSet();
	}

	/**
	 * Will return int array size of 5.
	 * First two segments are humidity (integral & decimal). Following two
	 * are temperature read in celsius (integral & decimal) and the last segment
	 * is the checksum which is the sum of the 4 first segments.
	 *
	 * @return the data or null if query failed
	 */
	private synchronized DataHolder queryData() {
		long now = System.currentTimeMillis();
		if (lastData != null && (lastQueryTime + 2000) > now) {
			return lastData;
		}
		int[] data = new int[5];
		PinState lastPinState = PinState.HIGH;
		int pulseCount = 0;

		// send a quick pulse to initiate
		// sensor to start sending data pulses
		multi.setMode(PinMode.DIGITAL_OUTPUT);
		multi.setState(PinState.LOW);
		Gpio.delay(18);
		multi.setState(PinState.HIGH);
		multi.setMode(PinMode.DIGITAL_INPUT);

		// start a loop which tries to determine
		// pulses using number of 'queryCycles' loops.
		// this loop needs to be fast as we're very
		// timing critical.
		for (int i = 0; i < queryCycles; i++) {
			int counter = 0;
			PinState inflightPinState = null;
			while ((inflightPinState = multi.getState()) == lastPinState && counter++ < 254) {
				// can't busy loop but sleep time to time
				if ((counter % 10) == 0) {
					Gpio.delayMicroseconds(1);
				}
			}

			lastPinState = inflightPinState;

			if (counter == 255) {
				break;
			}

			// ignore first 4 pulses
			if ((i >= 4) && (i % 2 == 0)) {
				/* shove each bit into the storage bytes */
				// as we should have 8 pulses per data block
				// shift those in to get the real value
				data[pulseCount / 8] <<= 1;
				// why bitwise or?
				if (counter > 16) {
					data[pulseCount / 8] |= 1;
				}
				pulseCount++;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Pulse count should be 40, was {}", pulseCount);
			log.debug("Raw data for 5 segments {} {} {} {} {}", data[0], data[1], data[2], data[3], data[4]);
		}

		DataHolder dataHolder = null;
		if ((pulseCount >= 40) && evaluateFletcherChecksum(data)) {
			float h = (float) ((data[0] << 8) + data[1]) / 10;
			if (h > 100) {
				h = data[0];
			}
			float c = (float) (((data[2] & 0x7F) << 8) + data[3]) / 10;
			if (c > 125) {
				c = data[2];
			}
			if ((data[2] & 0x80) != 0) {
				c = -c;
			}
			if (log.isDebugEnabled()) {
				log.debug("Get Humidity {} Temp {}", h, c);
			}
			dataHolder = new DataHolder(h, c);
		}
		lastQueryTime = now;
		if (dataHolder != null) {
			lastData = dataHolder;
		}
		return dataHolder;
	}

	private static boolean evaluateFletcherChecksum(int[] data) {
		return (data[4] == ((data[0] + data[1] + data[2] + data[3]) & 0xFF));
	}

	private static class DataHolder {
		private float humidity;
		private float temperature;

		public DataHolder(float humidity, float temperature) {
			this.humidity = humidity;
			this.temperature = temperature;
		}

		public float getHumidity() {
			return humidity;
		}

		public float getTemperature() {
			return temperature;
		}

		@Override
		public String toString() {
			return "DataHolder [humidity=" + humidity + ", temperature=" + temperature + "]";
		}
	}
}

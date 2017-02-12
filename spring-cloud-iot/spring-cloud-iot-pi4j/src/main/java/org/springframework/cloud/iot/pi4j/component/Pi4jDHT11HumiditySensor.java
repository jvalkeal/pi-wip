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
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.component.sensor.Humidity;
import org.springframework.cloud.iot.component.sensor.HumiditySensor;
import org.springframework.cloud.iot.component.sensor.Temperature;
import org.springframework.cloud.iot.component.sensor.TemperatureSensor;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;
import org.springframework.cloud.iot.support.ReactiveSensorValue;
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
	private final ReactiveSensorValue<DataHolder> sensorValue;
	private final Temperature temperature;
	private final Humidity humidity;
	private final Duration duration;

	public Pi4jDHT11HumiditySensor(GpioPinDigitalMultipurpose multi) {
		Assert.notNull(multi, "GpioPinDigitalMultipurpose must be set");
		this.multi = multi;
		this.duration = Duration.ofSeconds(2);
		this.sensorValue = new ReactiveSensorValue<>(new Callable<DataHolder>() {

			@Override
			public DataHolder call() throws Exception {
				return queryData();
			}
		}, duration);
		this.temperature = new Temperature() {

			@Override
			public Double getValue() {
				return (double) sensorValue.getValue().getTemperature();
			}

			@Override
			public Mono<Double> asMono() {
				return sensorValue.asMono().map(m -> (double)m.getTemperature());
			}

			@Override
			public Flux<Double> asFlux() {
				return sensorValue.asFlux().map(m -> (double)m.getTemperature());
			}
		};
		this.humidity = new Humidity() {

			@Override
			public Double getValue() {
				return (double) sensorValue.getValue().getHumidity();
			}

			@Override
			public Mono<Double> asMono() {
				return sensorValue.asMono().map(m -> (double)m.getHumidity());
			}

			@Override
			public Flux<Double> asFlux() {
				return sensorValue.asFlux().map(m -> (double)m.getHumidity());
			}
		};
	}

	@Override
	public String getName() {
		return "DHT11";
	}

	@Override
	public Temperature getTemperature() {
		return temperature;
	}

	@Override
	public Humidity getHumidity() {
		return humidity;
	}

	@Override
	protected void onInit() throws Exception {
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

		// send a pulse for sensor to initiate sending data pulses
		multi.setMode(PinMode.DIGITAL_OUTPUT);
		multi.setState(PinState.LOW);
		// dht11 spec requires this to be atleast 18 ms
		Gpio.delay(18);
		multi.setState(PinState.HIGH);
		multi.setMode(PinMode.DIGITAL_INPUT);

		// start a loop which tries to determine
		// pulses using number of 'queryCycles' loops.
		// this loop needs to be fast as we're very
		// timing critical.
		// we're expecting 2 pulses and then 40 pulses so
		// need to query atleast 85 times.
		// 26-28 us pulse length should indicate 0
		// 70 us pulse length should indicate 1
		int[] pulseLenghts = new int[queryCycles];
		for (int i = 0; i < queryCycles; i++) {
			int counter = 0;
			PinState inflightPinState = null;
			while ((inflightPinState = multi.getState()) == lastPinState && counter++ < 254) {
				// looks like busy looping is more reliable than sleeping
				// as getState() probably is not that fast.
			}

			// stash last pin state
			lastPinState = inflightPinState;

			// track lengths by counters
			pulseLenghts[i] = counter;
			if (counter == 255) {
				// got to end, invalid or real end
				break;
			}

			// ignore first 2 pulses
			if ((i >= 4) && (i % 2 == 0)) {
				/* shove each bit into the storage bytes */
				// as we should have 8 pulses per data block
				// shift those in to get the real value
				data[pulseCount / 8] <<= 1;
				// we expect that if get larger counter, pulse is longer, this
				// it needs to be pulse having value 1, so bitwise xor data.
				if (counter > 16) {
					data[pulseCount / 8] |= 1;
				}
				pulseCount++;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Pulse Lenghts by counters {}", Arrays.toString(pulseLenghts));
			log.debug("Pulse count should be 40, was {}", pulseCount);
			log.debug("Raw data for 5 segments {} {} {} {} {}", data[0], data[1], data[2], data[3], data[4]);
		}

		// before returning data we check that
		// 1. got 40 pulses
		// 2. we actually have pulses whose lengths looks normal, too shorts may mean just noise in channel
		// 3. evaluate checksum
		DataHolder dataHolder = null;
		if ((pulseCount >= 40) && expectNotTooShortPulses(pulseLenghts) && evaluateFletcherChecksum(data)) {
			// combine integral and decimal parts for hum
			float h = (float) ((data[0] << 8) + data[1]) / 10;
			if (h > 100) {
				h = data[0];
			}
			// combine integral and decimal parts for temp
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
		} else {
			log.debug("Data checksum invalid or pulse count too low");
		}
		lastQueryTime = now;
		if (dataHolder != null) {
			lastData = dataHolder;
			return lastData;
		}
		return null;
	}

	private static boolean expectNotTooShortPulses(int[] pulseLenghts) {
		// may be noise or something else which is causing too quick pulses
		// which arguably is just too quick and false data
		// this would cause all bits to be zero and Fletcher checksum
		// will not fail on that and we'd get zero temp/hum
		for (int i = 0; i < pulseLenghts.length; i++) {
			// looks like 10 is relatively good value to see
			// if pulses were too quick
			if (pulseLenghts[i] != 255 && pulseLenghts[i] > 10) {
				return true;
			}
		}
		return false;
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
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Float.floatToIntBits(humidity);
			result = prime * result + Float.floatToIntBits(temperature);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DataHolder other = (DataHolder) obj;
			if (Float.floatToIntBits(humidity) != Float.floatToIntBits(other.humidity))
				return false;
			if (Float.floatToIntBits(temperature) != Float.floatToIntBits(other.temperature))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "DataHolder [humidity=" + humidity + ", temperature=" + temperature + "]";
		}
	}
}

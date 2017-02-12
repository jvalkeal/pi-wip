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
package org.springframework.cloud.iot.pi4j;

import java.time.Duration;
import java.util.concurrent.Callable;

import org.springframework.cloud.iot.component.sensor.Temperature;
import org.springframework.cloud.iot.component.sensor.TemperatureSensor;
import org.springframework.cloud.iot.pi4j.support.Termistor;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;
import org.springframework.cloud.iot.support.ReactiveSensorValue;

import com.pi4j.temperature.TemperatureScale;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@code Pi4jPCF8591TemperatureSensor} is a {@link TemperatureSensor} using {@link Termistor}
 * as a backing component.
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jPCF8591TemperatureSensor extends LifecycleObjectSupport implements TemperatureSensor {

	private final String name;
	private final Termistor termistor;
	private final ReactiveSensorValue<Double> sensorValue;
	private final Temperature temperature;
	private final Duration duration;

	/**
	 * Instantiates a new pi4j pc f8591 temperature sensor.
	 *
	 * @param name the name
	 * @param termistor the termistor
	 */
	public Pi4jPCF8591TemperatureSensor(String name, Termistor termistor) {
		this.name = name;
		this.termistor = termistor;
		this.duration = Duration.ofSeconds(1);
		this.sensorValue = new ReactiveSensorValue<>(new Callable<Double>() {

			@Override
			public Double call() throws Exception {
				return termistor.getTemperature(TemperatureScale.CELSIUS);
			}
		}, duration);
		this.temperature = new Temperature() {

			@Override
			public Double getValue() {
				return (double) sensorValue.getValue();
			}

			@Override
			public Mono<Double> asMono() {
				return sensorValue.asMono();
			}

			@Override
			public Flux<Double> asFlux() {
				return sensorValue.asFlux();
			}
		};
	}

	@Override
	public String getName() {
		return name;
	}

//	@Override
//	public double getTemperature() {
//		return termistor.getTemperature(TemperatureScale.CELSIUS);
//	}

	@Override
	public Temperature getTemperature() {
		return temperature;
	}

	@Override
	protected void onInit() throws Exception {
		this.sensorValue.afterPropertiesSet();
	}

//	@Override
//	public Flux<Double> temperatureAsFlux() {
//		return sensorValue.asFlux();
//	}
//
//	@Override
//	public Mono<Double> temperatureAsMono() {
//		return sensorValue.asMono();
//	}
}

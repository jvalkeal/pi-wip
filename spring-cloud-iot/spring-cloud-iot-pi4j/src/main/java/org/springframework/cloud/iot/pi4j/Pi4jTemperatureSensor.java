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
import org.springframework.cloud.iot.support.LifecycleObjectSupport;
import org.springframework.cloud.iot.support.ReactiveSensorValue;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@code Pi4jTemperatureSensor} uses Pi4J's {@link com.pi4j.component.temperature.TemperatureSensor}
 * and setups a reactive access to that sensor.
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jTemperatureSensor extends LifecycleObjectSupport implements TemperatureSensor {

	private String name;
	private com.pi4j.component.temperature.TemperatureSensor sensor;
	private final ReactiveSensorValue<Double> sensorValue;
	private final Temperature temperature;

	/**
	 * Instantiates a new pi4j temperature sensor.
	 *
	 * @param name the name
	 * @param sensor the sensor
	 */
	public Pi4jTemperatureSensor(String name, com.pi4j.component.temperature.TemperatureSensor sensor) {
		this(name, sensor, Duration.ofSeconds(1));
	}

	/**
	 * Instantiates a new pi4j temperature sensor.
	 *
	 * @param name the name
	 * @param sensor the sensor
	 * @param duration the duration for polling internal
	 */
	public Pi4jTemperatureSensor(String name, com.pi4j.component.temperature.TemperatureSensor sensor, Duration duration) {
		this.name = name;
		this.sensor = sensor;
		this.sensorValue = new ReactiveSensorValue<>(new Callable<Double>() {

			@Override
			public Double call() throws Exception {
				return sensor.getTemperature();
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
	protected void onInit() throws Exception {
		this.sensorValue.afterPropertiesSet();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Temperature getTemperature() {
		return temperature;
	}
}

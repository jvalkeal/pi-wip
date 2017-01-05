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

import org.springframework.cloud.iot.component.TemperatureSensor;
import org.springframework.cloud.iot.pi4j.support.Termistor;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;
import org.springframework.cloud.iot.support.SensorValue;

import com.pi4j.component.temperature.impl.Tmp102;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jPCF8591TemperatureSensor extends LifecycleObjectSupport implements TemperatureSensor {

	private String name;
//	private Tmp102 tmp102;
	private Termistor termistor;

	private SensorValue<Double> sensorValue;
	private final Duration duration;

	/**
	 * Instantiates a new pi4j pc f8591 temperature sensor.
	 *
	 * @param name the name
	 * @param termistor the termistor
	 */
	public Pi4jPCF8591TemperatureSensor(String name, Termistor termistor) {
		this.name = name;
//		this.tmp102 = tmp102;
		this.termistor = termistor;
		this.duration = Duration.ofSeconds(1);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getTemperature() {
//		double analogVal = tmp102.getTemperature();
//		double Vr = 5 * analogVal / 255;
//		double Rt = 10000 * Vr / (5 - Vr);
//		double t = 1 / (((Math.log(Rt / 10000)) / 3950) + (1 / (273.15 + 25)));
//		t = t - 273.15;
//		return t;
		return termistor.getTemperature();
	}

	@Override
	protected void onInit() throws Exception {
		this.sensorValue = new SensorValue<>(new Callable<Double>() {

			@Override
			public Double call() throws Exception {
				return getTemperature();
			}
		}, duration);
		this.sensorValue.afterPropertiesSet();
	}

	@Override
	public Flux<Double> temperatureAsFlux() {
		return sensorValue.asFlux();
	}

	@Override
	public Mono<Double> temperatureAsMono() {
		return sensorValue.asMono();
	}
}

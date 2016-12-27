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
import org.springframework.cloud.iot.support.SensorValue;

import reactor.core.publisher.Flux;
import reactor.core.publisher.TopicProcessor;

public class Pi4jTemperatureSensor implements TemperatureSensor {

	private String name;
	private com.pi4j.component.temperature.TemperatureSensor sensor;
	private SensorValue<Double> sensorValue;

	public Pi4jTemperatureSensor(String name, com.pi4j.component.temperature.TemperatureSensor sensor) {
		this.name = name;
		this.sensor = sensor;
		this.sensorValue = new SensorValue<>(new Callable<Double>() {

			@Override
			public Double call() throws Exception {
				return sensor.getTemperature();
			}
		});
		try {
			this.sensorValue.afterPropertiesSet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getTemperature() {
		return sensor.getTemperature();
	}

	@Override
	public Flux<Double> asFlux() {
		return sensorValue.asFlux();
//		TopicProcessor<Double> topic = TopicProcessor.create();
//		Flux.interval(Duration.ofSeconds(2)).doOnNext(i -> {
//			topic.onNext(sensor.getTemperature());
//		}).subscribe();
//		return topic;
	}
}

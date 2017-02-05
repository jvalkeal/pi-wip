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
package org.springframework.cloud.iot.test.fake;

import java.time.Duration;
import java.util.concurrent.Callable;

import org.springframework.cloud.iot.component.sensor.Temperature;
import org.springframework.cloud.iot.component.sensor.TemperatureSensor;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;
import org.springframework.cloud.iot.support.ReactiveSensorValue;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Fake {@link TemperatureSensor} which just emits
 * random values between 20 and 21 once in every second.
 *
 * @author Janne Valkealahti
 *
 */
public class FakeTemperatureSensor extends LifecycleObjectSupport implements TemperatureSensor {

	private final ReactiveSensorValue<Double> sensorValue;
	private final Temperature temperature;

	public FakeTemperatureSensor() {
		this.sensorValue = new ReactiveSensorValue<>(new Callable<Double>() {

			@Override
			public Double call() throws Exception {
				return 20 + Math.random() * 1.0;
			}
		}, Duration.ofSeconds(1));
		this.temperature = new Temperature() {

			@Override
			public Double getValue() {
				return sensorValue.getValue();
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
		return "fake-temperature-sensor";
	}

	@Override
	public Temperature getTemperature() {
		return temperature;
	}

//	@Override
//	public Temperature getTemperature() {
//		return new Temperature() {
//
//			@Override
//			public Double value() {
//				return getSensorValue();
//			}
//
//			@Override
//			public Mono<Double> mono() {
//				return sensorValue.asMono();
//			}
//
//			@Override
//			public Flux<Double> flux() {
//				return sensorValue.asFlux();
//			}
//		};
//	}
//
//	private double getSensorValue() {
//		return 20 + Math.random() * 1.0;
//	}

//	@Override
//	public double getTemperature() {
//		return 20 + Math.random() * 1.0;
//	}
//
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

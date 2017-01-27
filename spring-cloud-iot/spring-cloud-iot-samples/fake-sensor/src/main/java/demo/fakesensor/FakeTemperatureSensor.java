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
package demo.fakesensor;

import java.time.Duration;
import java.util.concurrent.Callable;

import org.springframework.cloud.iot.component.TemperatureSensor;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;
import org.springframework.cloud.iot.support.SensorValue;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Fake {@link TemperatureSensor} which just emits
 * random values between 20 and 21.
 *
 * @author Janne Valkealahti
 *
 */
public class FakeTemperatureSensor extends LifecycleObjectSupport implements TemperatureSensor {

	private SensorValue<Double> sensorValue;

	@Override
	protected void onInit() throws Exception {
		this.sensorValue = new SensorValue<>(new Callable<Double>() {

			@Override
			public Double call() throws Exception {
				return getTemperature();
			}
		}, Duration.ofSeconds(1));
		this.sensorValue.afterPropertiesSet();
	}

	@Override
	public String getName() {
		return "fake-temperature-sensor";
	}

	@Override
	public double getTemperature() {
		return 20 + Math.random() * 1.0;
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

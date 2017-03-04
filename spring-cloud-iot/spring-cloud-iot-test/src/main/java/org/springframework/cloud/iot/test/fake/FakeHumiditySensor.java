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

import org.springframework.cloud.iot.component.sensor.Humidity;
import org.springframework.cloud.iot.component.sensor.HumiditySensor;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;
import org.springframework.cloud.iot.support.ReactiveSensorValue;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Fake {@link HumiditySensor} which just emits
 * random values between 25 and 50 once in every second.
 *
 * @author Janne Valkealahti
 *
 */
public class FakeHumiditySensor extends LifecycleObjectSupport implements HumiditySensor {

	private final ReactiveSensorValue<Double> sensorValue;
	private final Humidity humidity;

	public FakeHumiditySensor() {
		this.sensorValue = new ReactiveSensorValue<>(new Callable<Double>() {

			@Override
			public Double call() throws Exception {
				return 25 + Math.random() * 25.0;
			}
		}, Duration.ofSeconds(1));

		this.humidity = new Humidity() {

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
		return "fake-humidity-sensor";
	}

	@Override
	public Humidity getHumidity() {
		return humidity;
	}
}

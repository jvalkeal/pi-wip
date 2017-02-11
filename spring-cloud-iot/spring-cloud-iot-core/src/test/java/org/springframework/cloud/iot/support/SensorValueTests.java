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
package org.springframework.cloud.iot.support;

import java.time.Duration;
import java.util.concurrent.Callable;

import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class SensorValueTests {

	private final Callable<String> helloCallable1 = new Callable<String>() {
		private int i = 0;
		@Override
		public String call() throws Exception {
			return "hello" + (i++);
		}
	};

	@Test
	public void testSimpleValues() throws Exception {
		ReactiveSensorValue<String> sensorValue = new ReactiveSensorValue<>(helloCallable1, Duration.ofSeconds(1));
		sensorValue.afterPropertiesSet();
		Flux<String> flux = sensorValue.asFlux();
		StepVerifier.create(flux)
			.expectNext("hello0")
			.expectNext("hello1")
			.thenCancel()
			.verify();
	}

	@Test
	public void testSimpleValuesTwice() throws Exception {
		ReactiveSensorValue<String> sensorValue = new ReactiveSensorValue<>(helloCallable1, Duration.ofSeconds(1));
		sensorValue.afterPropertiesSet();
		Flux<String> flux = sensorValue.asFlux();
		StepVerifier.create(flux)
			.expectNext("hello0")
			.thenCancel()
			.verify();

		StepVerifier.create(flux)
			.expectNext("hello1")
			.thenCancel()
			.verify();
	}

}

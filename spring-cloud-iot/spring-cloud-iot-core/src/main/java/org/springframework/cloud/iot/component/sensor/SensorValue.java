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
package org.springframework.cloud.iot.component.sensor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface for arbitrary sensor value which can be
 * returned either as a plain value, {@link Flux} or {@link Mono}.
 *
 * @param <T> the type of a sensor value
 */
public interface SensorValue<T> {

	/**
	 * Get sensor value as a plain value.
	 *
	 * @return the plain sensor value
	 */
	T getValue();

	/**
	 * Get sensor values as {@link Flux}.
	 *
	 * @return the sensor values as flux
	 */
	Flux<T> asFlux();

	/**
	 * Get sensor value as a {@link Mono}.
	 *
	 * @return the plain sensor value mono
	 */
	Mono<T> asMono();
}

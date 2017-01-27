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
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import reactor.core.Disposable;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@code SensorValue} is a helper class to help working with
 * sensor values.
 *
 * Sensor values can be requests as raw values, as a {@link Mono} or
 * as a {@link Flux}. If there are active subscribers to a flux sensor
 * polling is done by intervals and {@link #getValue()} and {@link #asMono()}
 * may return cached value.
 *
 * @author Janne Valkealahti
 *
 * @param <T> the type of a sensor value
 */
public class SensorValue<T> implements InitializingBean {

	private DirectProcessor<T> processor = DirectProcessor.create();
	private Flux<T> sensorFlux;
	private final AtomicInteger subscriberCount = new AtomicInteger();
	private Disposable disposable;
	private final Callable<T> valueCallable;
	private final Duration duration;
	private T last;

	/**
	 * Instantiates a new sensor value.
	 *
	 * @param valueCallable the callable producing sensor value
	 * @param duration the duration how often sensor value is requested
	 */
	public SensorValue(Callable<T> valueCallable, Duration duration) {
		Assert.notNull(valueCallable, "valueCallable must be set");
		Assert.notNull(duration, "duration must be set");
		this.valueCallable = valueCallable;
		this.duration = duration;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Flux<Long> intervalFlux = Flux.interval(duration).doOnNext(i -> {
			T value = getValueInternal();
			if (!ObjectUtils.nullSafeEquals(value, last)) {
				System.out.println("DDDD1 " + value);
				processor.onNext(value);
			}
			last = value;
		});

		sensorFlux = processor.doOnSubscribe(c -> {
			if (subscriberCount.getAndIncrement() == 0) {
				disposable = intervalFlux.subscribe();
			}
		}).doFinally(c -> {
			if (subscriberCount.decrementAndGet() == 0) {
				disposable.dispose();
				last = null;
			}
		});
	}

	/**
	 * Gets the last sensor value.
	 *
	 * @return the sensor value
	 */
	public T getValue() {
		T l = last;
		if (subscriberCount.get() > 0 && l != null) {
			return l;
		} else {
			return getValueInternal();
		}
	}

	/**
	 * Gets the sensor values as a {@link Flux}.
	 *
	 * @return the sensor values flux.
	 */
	public Flux<T> asFlux() {
		T l = last;
		if (l != null) {
			return Flux.merge(Flux.just(l), sensorFlux);
		} else {
			return sensorFlux;
		}
	}

	/**
	 * Gets the last sensor value as a {@link Mono}.
	 *
	 * @return the sensor value mono
	 */
	public Mono<T> asMono() {
		T l = last;
		if (subscriberCount.get() > 0 && l != null) {
			return Mono.just(l);
		} else {
			return Mono.fromCallable(valueCallable);
		}
	}

	private T getValueInternal() {
		try {
			return valueCallable.call();
		} catch (Exception e) {
			throw new RuntimeException("Unable to query value", e);
		}
	}
}

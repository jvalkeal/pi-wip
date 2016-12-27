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
import org.springframework.util.ObjectUtils;

import reactor.core.Disposable;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SensorValue<T> implements InitializingBean {

	private DirectProcessor<T> processor = DirectProcessor.create();
	private Flux<T> sensorFlux;
	private final AtomicInteger subscriberCount = new AtomicInteger();
	private Disposable disposable;
	private Callable<T> valueCallable;
	private T last;

	public SensorValue(Callable<T> valueCallable) {
		this.valueCallable = valueCallable;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Flux<Long> intervalFlux = Flux.interval(Duration.ofSeconds(1)).doOnNext(i -> {
			T value = getValueInternal();
			if (!ObjectUtils.nullSafeEquals(value, last)) {
				processor.onNext(getValue());
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

	public T getValue() {
		T l = last;
		if (subscriberCount.get() > 0 && l != null) {
			return l;
		} else {
			return getValueInternal();
		}
	}

	public Flux<T> asFlux() {
		T l = last;
		if (l != null) {
			return Flux.merge(Flux.just(l), sensorFlux);
		} else {
			return sensorFlux;
		}
	}

	public Mono<T> asMono() {
		return Mono.fromCallable(valueCallable);
	}

	private T getValueInternal() {
		try {
			return valueCallable.call();
		} catch (Exception e) {
			throw new RuntimeException("Unable to query value", e);
		}
	}
}

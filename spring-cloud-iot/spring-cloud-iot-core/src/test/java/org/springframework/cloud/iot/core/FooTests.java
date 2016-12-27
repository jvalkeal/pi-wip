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
package org.springframework.cloud.iot.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.reactivestreams.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.Disposable;
import reactor.core.publisher.BlockingSink;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.TopicProcessor;


public class FooTests {

	private final static Logger log = LoggerFactory.getLogger(FooTests.class);

	@Test
	public void testBar1() throws InterruptedException {
		TopicProcessor<Double> topic = TopicProcessor.create();
		Flux<Double> flux = topic;

		Flux.interval(Duration.ofSeconds(1)).doOnNext(i -> {
			log.info("Interval {}", i);
			topic.onNext(Math.random());
		}).subscribe();

		Disposable subscribe1 = flux.subscribe(temp -> {
			log.info("set1 temp {}", temp);
		});
		Thread.sleep(2000);
		subscribe1.dispose();
		Thread.sleep(4000);
	}

	@Test
	public void testBar2() throws InterruptedException {
		EmitterProcessor<Double> emitter = EmitterProcessor.create();
		BlockingSink<Double> sink = emitter.connectSink();
		Flux<Double> flux = emitter;

		Flux.interval(Duration.ofSeconds(1)).doOnNext(i -> {
			log.info("Interval {}", i);
			emitter.onNext(Math.random());
//			sink.next(Math.random());
		}).subscribe();

		Disposable subscribe1 = flux.subscribe(temp -> {
			log.info("set1 temp {}", temp);
		});

		Thread.sleep(2000);
		subscribe1.dispose();
		Thread.sleep(4000);
	}

	@Test
	public void testBar3() throws InterruptedException {
		TopicProcessor<Double> topic = TopicProcessor.create();
		Processor<Double, Double> processor = topic;
		Flux<Double> flux = topic;

		flux.subscribe(temp -> {
			log.info("temp {}", temp);
		});

		processor.onNext(Math.random());
		processor.onNext(Math.random());
		Thread.sleep(2000);
	}

	@Test
	public void testBar4() throws InterruptedException {
		EmitterProcessor<Double> emitter = EmitterProcessor.create();
		Processor<Double, Double> processor = emitter;
		Flux<Double> flux = emitter;

		flux.subscribe(temp -> {
			log.info("temp {}", temp);
		});

		processor.onNext(Math.random());
		processor.onNext(Math.random());
		Thread.sleep(2000);
	}

	@Test
	public void testBar5() throws InterruptedException {
		DirectProcessor<Double> emitter = DirectProcessor.create();
		Processor<Double, Double> processor = emitter;
		Flux<Double> flux = emitter;

		flux.subscribe(temp -> {
			log.info("temp {}", temp);
		});

		processor.onNext(Math.random());
		processor.onNext(Math.random());
		Thread.sleep(2000);
	}

	@Test
	public void sensor() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<SignalType> terminatingSignal = new AtomicReference<>();
		EmitterProcessor<Long> dispatcher = EmitterProcessor.create();

		Flux<Long> sensor = Flux.interval(Duration.ofSeconds(1))
								.doOnNext(i -> System.out.println("tick " + i))
				.doFinally(f -> {
					terminatingSignal.set(f);
					latch.countDown();
				});

		sensor.subscribe(dispatcher);

		Disposable d1 = dispatcher.subscribe(i -> System.out.println("first consumer " + i));
		Thread.sleep(4000);
		Disposable d2 = dispatcher.subscribe(i -> System.out.println("second consumer " + i));
		Thread.sleep(2000);
		d1.dispose();
		Thread.sleep(2000);
		d2.dispose();
		if (latch.await(4, TimeUnit.SECONDS)) {
			System.out.println("Sensor terminated by signal " + terminatingSignal.get());
		}
	}

	@Test
	public void testBar6() throws Exception {
		SensorTest sensor = new SensorTest();
		sensor.setup();
		Flux<Double> flux = sensor.asFlux();

		Disposable subscribe1 = flux.subscribe(temp -> {
			log.info("temp 1 {}", temp);
		});
		Thread.sleep(3000);

		Disposable subscribe2 = flux.subscribe(temp -> {
			log.info("temp 2 {}", temp);
		});
		Thread.sleep(3000);

		subscribe2.dispose();
		Thread.sleep(3000);
		subscribe1.dispose();
		Thread.sleep(3000);

	}

	@Test
	public void testBar7() throws Exception {
		SensorTest sensor = new SensorTest();
		sensor.setup();
		Flux<Double> flux = sensor.asFlux();

		ArrayList<Disposable> subscribes = new ArrayList<>();

		for (int i = 0; i < 2; i++) {
			Disposable subscribe = flux.subscribe(temp -> {
				log.info("temp 1 {}", temp);
			});
			subscribes.add(subscribe);
		}

		Thread.sleep(3000);

		for (Disposable d : subscribes) {
			d.dispose();
		}
		Thread.sleep(3000);

		subscribes.clear();
		for (int i = 0; i < 2; i++) {
			Disposable subscribe = flux.subscribe(temp -> {
				log.info("temp 1 {}", temp);
			});
			subscribes.add(subscribe);
		}

		Thread.sleep(3000);

		for (Disposable d : subscribes) {
			d.dispose();
		}
		Thread.sleep(3000);

		subscribes.clear();
		for (int i = 0; i < 1; i++) {
			Disposable subscribe = flux.subscribe(temp -> {
				log.info("temp 1 {}", temp);
			});
			subscribes.add(subscribe);
		}

		Thread.sleep(3000);

		for (Disposable d : subscribes) {
			d.dispose();
		}
		Thread.sleep(3000);
	}

	private static class SensorTest {
		DirectProcessor<Double> topic = DirectProcessor.create();
		Flux<Long> intervalFlux;
		Flux<Double> flux;
		final AtomicInteger count = new AtomicInteger();
		Disposable disposable;

		void setup() {
			intervalFlux = Flux.interval(Duration.ofSeconds(1)).doOnNext(i -> {
				double temp = Math.random();
				log.info("emit temp {}", temp);
				topic.onNext(temp);
			});

			flux = topic.doOnSubscribe(c -> {
				int i = count.getAndIncrement();
				log.info("Subscription {} {}", c, i);
				if (i == 0) {
					disposable = intervalFlux.subscribe();
				}
			}).doFinally(c -> {
				int i = count.decrementAndGet();
				log.info("Finally {} {}", c, i);
				if (i == 0) {
					disposable.dispose();
				}
			});

		}

		Flux<Double> asFlux() {
			return flux;
		}
	}

	@Test
	public void testBar8() throws Exception {
		DirectProcessor<Double> topic = DirectProcessor.create();
		Flux<Double> flux = topic;

		flux.doOnSubscribe(s -> {
			log.info("Subscription {}", s);
		}).subscribe();

		flux.subscribe(temp -> {
			log.info("emit temp {}", temp);
		});

		topic.onNext(Math.random());

		Thread.sleep(2000);
	}

	@Test
	public void testBar9() throws Exception {
		Flux<Integer> flux = Flux.just(1,2,3)
				.doOnSubscribe(s -> {
					log.info("Subscription {}", s);
				}).doFinally(c -> {
					log.info("Finally {}", c);
				});
//		flux.doOnSubscribe(s -> {
//			log.info("Subscription {}", s);
//		});
		Disposable disposable1 = flux.subscribe(temp -> {
			log.info("emit temp {}", temp);
		});
		Disposable disposable2 = flux.subscribe(temp -> {
			log.info("emit temp {}", temp);
		});
//		flux.doOnSubscribe(s -> {
//			log.info("Subscription {}", s);
//		}).subscribe(temp -> {
//			log.info("emit temp {}", temp);
//		});
		Thread.sleep(2000);
		disposable1.dispose();
		disposable2.dispose();
		Thread.sleep(2000);
	}

	@Test
	public void testBar10() throws Exception {
		Flux<Integer> flux = Flux.just(1,2,3)
				.doOnSubscribe(s -> {
					log.info("Subscription {}", s);
				});
		flux.subscribe(v -> {
			log.info("emit value {}", v);
		});
		flux.subscribe(v -> {
			log.info("emit value {}", v);
		});
		Thread.sleep(2000);
	}

	@Test
	public void testBar11() throws Exception {
		Flux<Integer> flux = Flux.just(1,2,3);
		flux.doOnSubscribe(s -> {
			log.info("Subscription {}", s);
		}).subscribe();
		flux.subscribe(v -> {
			log.info("emit value {}", v);
		});
		flux.subscribe(v -> {
			log.info("emit value {}", v);
		});
		Thread.sleep(2000);
	}

	@Test
	public void testFoo() {

		int[] colors = new int[] { 0xFF00, 0x00FF, 0x0FF0, 0xF00F };
		for (int color : colors) {
			int R_val = color  >> 8;
			int G_val = color & 0x00FF;

			int R_val2 = map(R_val, 0, 255, 0, 100);
			int G_val2 = map(G_val, 0, 255, 0, 100);

			log.info("R_val {} {}", R_val, R_val2);
			log.info("G_val {} {}", G_val, G_val2);
		}

	}

	@Test
	public void testXxx() throws IOException {
		String deviceName = new String(Files.readAllBytes(new File("/tmp/", "data.txt").toPath())).trim();
		log.info("XXX '{}'", deviceName);
	}

	private static int map(int x, int in_min, int in_max, int out_min, int out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
//	def map(x, in_min, in_max, out_min, out_max):
//		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min
}

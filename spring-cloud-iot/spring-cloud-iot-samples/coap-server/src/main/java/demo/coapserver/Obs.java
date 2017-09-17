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
package demo.coapserver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.springframework.cloud.iot.coap.annotation.CoapController;
import org.springframework.cloud.iot.coap.annotation.CoapObservable;

import reactor.core.publisher.Flux;

@CoapController
public class Obs {

	private final Flux<String> timestamps1 = Flux.interval(Duration.ofSeconds(5)).map(i -> {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		return "XXX1 " + dateFormat.format(new Date());
	});

	private final Flux<String> timestamps2 = Flux.interval(Duration.ofSeconds(5)).map(i -> {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		return "XXX2 " + dateFormat.format(new Date());
	});

	@CoapObservable(path = "/obs1")
	public Flux<String> obs1() {
		return Flux.from(timestamps1);
	}

	@CoapObservable(path = "/obs2")
	public Flux<String> obs2() {
		return Flux.from(timestamps2);
	}
}

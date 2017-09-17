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
package org.springframework.cloud.iot.coap.server.result.method.annotation;

import org.springframework.cloud.iot.coap.annotation.CoapObservable;
import org.springframework.cloud.iot.coap.server.HandlerResult;
import org.springframework.cloud.iot.coap.server.HandlerResultHandler;
import org.springframework.cloud.iot.coap.server.ServerCoapExchange;
import org.springframework.cloud.iot.coap.server.ServerCoapObservableContext;
import org.springframework.cloud.iot.coap.server.ServerCoapResponse;
import org.springframework.core.MethodParameter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@code HandlerResultHandler} that handles return values from methods annotated
 * with {@code @CoapObservable} writing to the body of the request or response.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapObservableResultHandler implements HandlerResultHandler {

	@Override
	public boolean supports(HandlerResult result) {
		MethodParameter parameter = result.getReturnTypeSource();
		return parameter.getMethodAnnotation(CoapObservable.class) != null;
	}

	@Override
	public Mono<Void> handleResult(ServerCoapExchange exchange, HandlerResult result) {

		ServerCoapObservableContext observableContext = exchange.getObservableContext();
		Flux<?> f = (Flux<?>)result.getReturnValue();

		if (observableContext != null) {
			observableContext.setObservableSource(Flux.from(f));
		}

		ServerCoapResponse response = exchange.getResponse();

		Flux<?> ddd = f.take(1).doOnNext(c -> {
			response.setBody(c.toString().getBytes());
		});

		return Mono.from(ddd).then();
	}
}

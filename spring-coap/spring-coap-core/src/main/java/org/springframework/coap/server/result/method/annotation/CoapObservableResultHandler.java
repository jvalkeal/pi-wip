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
package org.springframework.coap.server.result.method.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.coap.annotation.CoapObservable;
import org.springframework.coap.server.HandlerResult;
import org.springframework.coap.server.HandlerResultHandler;
import org.springframework.coap.server.ServerCoapExchange;
import org.springframework.coap.server.ServerCoapObservableContext;
import org.springframework.coap.server.ServerCoapResponse;
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

	private static final Logger log = LoggerFactory.getLogger(CoapObservableResultHandler.class);

	@Override
	public boolean supports(HandlerResult result) {
		MethodParameter parameter = result.getReturnTypeSource();
		return parameter.getMethodAnnotation(CoapObservable.class) != null;
	}

	@Override
	public Mono<Void> handleResult(ServerCoapExchange exchange, HandlerResult result) {
		Flux<?> f = (Flux<?>)result.getReturnValue();
		ServerCoapResponse response = exchange.getResponse();
		ServerCoapObservableContext context = exchange.getObservableContext();

		log.info("XXX context {}", context);

		if (context != null) {
			log.info("XXX context result {}", context.getResult());
			if (context.getResult() != null) {
				response.setBody(context.getResult().toString().getBytes());
				log.info("XXX return result to response");
				return Mono.empty();
			} else {
				context.setObservableSource(Flux.from(f));
			}
		}

		log.info("XXX return default take 1");
		return Mono.from(f.take(1).doOnNext(c -> {
			response.setBody(c.toString().getBytes());
		})).then();
	}
}

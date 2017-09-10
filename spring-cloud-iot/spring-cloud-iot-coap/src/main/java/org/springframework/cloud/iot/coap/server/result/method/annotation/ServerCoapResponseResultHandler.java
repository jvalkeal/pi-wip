/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.iot.coap.server.result.method.annotation;

import org.springframework.cloud.iot.coap.server.HandlerResult;
import org.springframework.cloud.iot.coap.server.HandlerResultHandler;
import org.springframework.cloud.iot.coap.server.ServerCoapExchange;
import org.springframework.cloud.iot.coap.server.ServerCoapResponse;
import org.springframework.util.Assert;

import reactor.core.publisher.Mono;

public class ServerCoapResponseResultHandler implements HandlerResultHandler {

	@Override
	public boolean supports(HandlerResult result) {
//		return (result.getReturnValue() instanceof ServerResponse);
		return (result.getReturnValue() instanceof String);
	}

	@Override
	public Mono<Void> handleResult(ServerCoapExchange exchange, HandlerResult result) {
//		ServerResponse response = (ServerResponse) result.getReturnValue();
//		Assert.state(response != null, "No ServerCoapResponse");
//		return response.writeTo(exchange);
		ServerCoapResponse response = exchange.getResponse();

		response.setBody(((String)result.getReturnValue()).getBytes());
		return Mono.empty();
	}

}

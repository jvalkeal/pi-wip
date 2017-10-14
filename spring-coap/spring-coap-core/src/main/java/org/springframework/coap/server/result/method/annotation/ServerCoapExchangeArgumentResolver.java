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

import org.springframework.coap.server.ServerCoapExchange;
import org.springframework.coap.server.result.method.CoapHandlerMethodArgumentResolver;
import org.springframework.core.MethodParameter;

import reactor.core.publisher.Mono;

public class ServerCoapExchangeArgumentResolver implements CoapHandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> type = parameter.getParameterType();
		return ServerCoapExchange.class.isAssignableFrom(type);
	}

	@Override
	public Mono<Object> resolveArgument(MethodParameter parameter, ServerCoapExchange exchange) {
		Class<?> paramType = parameter.getParameterType();
		if (ServerCoapExchange.class.isAssignableFrom(paramType)) {
			return Mono.just(exchange);
		} else {
			// should never happen...
			throw new IllegalArgumentException(
					"Unknown parameter type: " + paramType + " in method: " + parameter.getMethod());
		}
	}

}

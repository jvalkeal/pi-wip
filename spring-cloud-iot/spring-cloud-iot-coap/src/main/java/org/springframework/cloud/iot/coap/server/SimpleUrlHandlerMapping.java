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
package org.springframework.cloud.iot.coap.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Mono;

public class SimpleUrlHandlerMapping implements HandlerMapping {

	private static final Logger log = LoggerFactory.getLogger(SimpleUrlHandlerMapping.class);

	public Object handlerxx;

	@Override
	public Mono<Object> getHandler(ServerCoapExchange exchange) {
		return getHandlerInternal(exchange).map(handler -> {
			return handler;
		});
	}

	public Mono<Object> getHandlerInternal(ServerCoapExchange exchange) {
		String uriPath = exchange.getRequest().getUriPath();

		Object handler;
		try {
			handler = handlerxx;
		}
		catch (Exception ex) {
			return Mono.error(ex);
		}

		if (handler != null && log.isDebugEnabled()) {
			log.debug("Mapping [" + uriPath + "] to " + handler);
		}
		else if (handler == null && log.isTraceEnabled()) {
			log.trace("No handler mapping found for [" + uriPath + "]");
		}

		return Mono.justOrEmpty(handler);
	}

}

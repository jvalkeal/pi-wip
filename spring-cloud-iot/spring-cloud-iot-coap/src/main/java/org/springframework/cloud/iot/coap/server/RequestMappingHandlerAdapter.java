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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.iot.coap.server.support.ControllerMethodResolver;
import org.springframework.util.Assert;

import reactor.core.publisher.Mono;

public class RequestMappingHandlerAdapter implements HandlerAdapter, InitializingBean {

	private ControllerMethodResolver methodResolver;

	@Override
	public boolean supports(Object handler) {
		return HandlerMethod.class.equals(handler.getClass());
	}

	@Override
	public Mono<HandlerResult> handle(ServerCoapExchange exchange, Object handler) {
		Assert.notNull(handler, "Expected handler");
		HandlerMethod handlerMethod = (HandlerMethod) handler;

		Mono<HandlerResult> invoke = this.methodResolver.getRequestMappingMethod(handlerMethod).invoke(exchange);

		return invoke;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.methodResolver = new ControllerMethodResolver();
	}
}

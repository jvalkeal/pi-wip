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
package org.springframework.coap.server.support;

import java.util.List;

import org.springframework.coap.server.HandlerMethod;
import org.springframework.coap.server.result.method.CoapHandlerMethodArgumentResolver;
import org.springframework.coap.server.result.method.InvocableHandlerMethod;

public class ControllerMethodResolver {

	private final List<CoapHandlerMethodArgumentResolver> requestMappingResolvers;

	public ControllerMethodResolver(List<CoapHandlerMethodArgumentResolver> requestMappingResolvers) {
		this.requestMappingResolvers = requestMappingResolvers;
	}

	public InvocableHandlerMethod getRequestMappingMethod(HandlerMethod handlerMethod) {
		InvocableHandlerMethod invocable = new InvocableHandlerMethod(handlerMethod);
		invocable.setArgumentResolvers(this.requestMappingResolvers);
		return invocable;
	}

}

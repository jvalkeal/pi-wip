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
package org.springframework.cloud.iot.coap.server.result.method;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.cloud.iot.coap.server.HandlerMethod;
import org.springframework.cloud.iot.coap.server.HandlerResult;
import org.springframework.cloud.iot.coap.server.ServerCoapExchange;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import reactor.core.publisher.Mono;

public class InvocableHandlerMethod extends HandlerMethod {

	public InvocableHandlerMethod(HandlerMethod handlerMethod) {
		super(handlerMethod);
	}

	public Mono<HandlerResult> invoke(ServerCoapExchange exchange) {

		try {
			Object value = doInvoke(new String[0]);
			HandlerResult result = new HandlerResult(this, value, getReturnType());
			return Mono.just(result);
		}
		catch (InvocationTargetException ex) {
			return Mono.error(ex.getTargetException());
		}
		catch (Throwable ex) {
			return Mono.error(new IllegalStateException(getInvocationErrorMessage(new String[0])));
		}


	}

	private Object doInvoke(Object[] args) throws Exception {
//		if (logger.isTraceEnabled()) {
//			logger.trace("Invoking '" + ClassUtils.getQualifiedMethodName(getMethod(), getBeanType()) +
//					"' with arguments " + Arrays.toString(args));
//		}
		ReflectionUtils.makeAccessible(getBridgedMethod());
		Method ddd1 = getBridgedMethod();
		Object ddd2 = getBean();
		Object returnValue = getBridgedMethod().invoke(getBean(), args);
//		if (logger.isTraceEnabled()) {
//			logger.trace("Method [" + ClassUtils.getQualifiedMethodName(getMethod(), getBeanType()) +
//					"] returned [" + returnValue + "]");
//		}
		return returnValue;
	}

	private String getInvocationErrorMessage(Object[] args) {
		String argumentDetails = IntStream.range(0, args.length)
				.mapToObj(i -> (args[i] != null ?
						"[" + i + "][type=" + args[i].getClass().getName() + "][value=" + args[i] + "]" :
						"[" + i + "][null]"))
				.collect(Collectors.joining(",", " ", " "));
		return "Failed to invoke handler method with resolved arguments:" + argumentDetails +
				"on " + getBridgedMethod().toGenericString();
	}

}

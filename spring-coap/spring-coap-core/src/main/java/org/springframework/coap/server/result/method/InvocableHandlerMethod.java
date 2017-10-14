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
package org.springframework.coap.server.result.method;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.coap.server.HandlerMethod;
import org.springframework.coap.server.HandlerResult;
import org.springframework.coap.server.ServerCoapExchange;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import reactor.core.publisher.Mono;

public class InvocableHandlerMethod extends HandlerMethod {

	private static final Logger log = LoggerFactory.getLogger(InvocableHandlerMethod.class);
	private static final Mono<Object[]> EMPTY_ARGS = Mono.just(new Object[0]);
	private static final Object NO_ARG_VALUE = new Object();
	private List<CoapHandlerMethodArgumentResolver> resolvers = new ArrayList<>();
	private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

	public InvocableHandlerMethod(HandlerMethod handlerMethod) {
		super(handlerMethod);
	}

	/**
	 * Configure the argument resolvers to use to use for resolving method
	 * argument values against a {@code ServerCoapExchange}.
	 *
	 * @param resolvers the argument resolvers
	 */
	public void setArgumentResolvers(List<CoapHandlerMethodArgumentResolver> resolvers) {
		this.resolvers.clear();
		this.resolvers.addAll(resolvers);
	}

	/**
	 * Return the configured argument resolvers.
	 *
	 * @return the configured argument resolvers
	 */
	public List<CoapHandlerMethodArgumentResolver> getResolvers() {
		return this.resolvers;
	}

	public Mono<HandlerResult> invoke(ServerCoapExchange exchange, Object... providedArgs) {
		return resolveArguments(exchange, providedArgs).flatMap(args -> {
			try {
				Object value = doInvoke(args);
				HandlerResult result = new HandlerResult(this, value, getReturnType());
				return Mono.just(result);
			}
			catch (InvocationTargetException ex) {
				return Mono.error(ex.getTargetException());
			}
			catch (Throwable ex) {
				return Mono.error(new IllegalStateException(getInvocationErrorMessage(new String[0])));
			}
		});
	}

	private Mono<Object[]> resolveArguments(ServerCoapExchange exchange, Object... providedArgs) {

		if (ObjectUtils.isEmpty(getMethodParameters())) {
			return EMPTY_ARGS;
		}
		try {
			List<Mono<Object>> argMonos = Stream.of(getMethodParameters())
					.map(param -> {
						param.initParameterNameDiscovery(this.parameterNameDiscoverer);
						return findProvidedArgument(param, providedArgs)
								.map(Mono::just)
								.orElseGet(() -> {
									CoapHandlerMethodArgumentResolver resolver = findResolver(param);
									return resolveArg(resolver, param, exchange);
								});

					})
					.collect(Collectors.toList());

			// Create Mono with array of resolved values...
			return Mono.zip(argMonos, argValues ->
					Stream.of(argValues).map(o -> o != NO_ARG_VALUE ? o : null).toArray());
		}
		catch (Throwable ex) {
			return Mono.error(ex);
		}
	}

	private Optional<Object> findProvidedArgument(MethodParameter parameter, Object... providedArgs) {
		if (ObjectUtils.isEmpty(providedArgs)) {
			return Optional.empty();
		}
		return Arrays.stream(providedArgs)
				.filter(arg -> parameter.getParameterType().isInstance(arg))
				.findFirst();
	}

	private CoapHandlerMethodArgumentResolver findResolver(MethodParameter param) {
		return this.resolvers.stream()
				.filter(r -> r.supportsParameter(param))
				.findFirst()
				.orElseThrow(() -> getArgumentError("No suitable resolver for", param, null));
	}

	private Mono<Object> resolveArg(CoapHandlerMethodArgumentResolver resolver, MethodParameter parameter,
			ServerCoapExchange exchange) {

		try {
			return resolver.resolveArgument(parameter, exchange)
					.defaultIfEmpty(NO_ARG_VALUE)
					.doOnError(cause -> {
						if (log.isDebugEnabled()) {
							log.debug(getDetailedErrorMessage("Failed to resolve", parameter), cause);
						}
					});
		}
		catch (Exception ex) {
			throw getArgumentError("Failed to resolve", parameter, ex);
		}
	}

	private IllegalStateException getArgumentError(String text, MethodParameter parameter, @Nullable Throwable ex) {
		return new IllegalStateException(getDetailedErrorMessage(text, parameter), ex);
	}

	private String getDetailedErrorMessage(String text, MethodParameter param) {
		return text + " argument " + param.getParameterIndex() + " of type '" +
				param.getParameterType().getName() + "' on " + getBridgedMethod().toGenericString();
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

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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.iot.coap.annotation.CoapController;
import org.springframework.cloud.iot.coap.annotation.CoapRequestMapping;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import reactor.core.publisher.Mono;

/**
 * An implementation of {@link HandlerMapping} that creates
 * {@link RequestMappingInfo} instances from class-level and method-level
 * {@link CoapRequestMapping @CoapRequestMapping} annotations.
 *
 * @author Janne Valkealahti
 *
 */
public class RequestMappingHandlerMapping extends ApplicationObjectSupport implements HandlerMapping, InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(RequestMappingHandlerMapping.class);

	private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";

	private HandlerMethod xxx;

	@Override
	public Mono<Object> getHandler(ServerCoapExchange exchange) {
		ServerCoapRequest request = exchange.getRequest();
		String uriPath = request.getUriPath();

		HandlerMethod ddd = xxx.createWithResolvedBean();
		return Mono.just(ddd);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initHandlerMethods();
	}

	protected void initHandlerMethods() {
		String[] beanNames = obtainApplicationContext().getBeanNamesForType(Object.class);

		for (String beanName : beanNames) {
			if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
				Class<?> beanType = null;
				try {
					beanType = obtainApplicationContext().getType(beanName);
				}
				catch (Throwable ex) {
					// An unresolvable bean type, probably from a lazy bean - let's ignore it.
					if (logger.isDebugEnabled()) {
						logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
					}
				}
				if (beanType != null && isHandler(beanType)) {
					detectHandlerMethods(beanName);
				}
			}
		}
	}

	protected boolean isHandler(Class<?> beanType) {
		return (AnnotatedElementUtils.hasAnnotation(beanType, CoapController.class) ||
				AnnotatedElementUtils.hasAnnotation(beanType, CoapRequestMapping.class));
	}

	protected void detectHandlerMethods(final Object handler) {
		Class<?> handlerType = (handler instanceof String ?
				obtainApplicationContext().getType((String) handler) : handler.getClass());

		if (handlerType != null) {
			final Class<?> userType = ClassUtils.getUserClass(handlerType);
			Map<Method, ?> methods = MethodIntrospector.selectMethods(userType,
					(MethodIntrospector.MetadataLookup<?>) method -> getMappingForMethod(method, userType));
			if (log.isDebugEnabled()) {
				log.debug(methods.size() + " request handler methods found on " + userType + ": " + methods);
			}
			methods.forEach((key, mapping) -> {
				Method invocableMethod = AopUtils.selectInvocableMethod(key, userType);
				xxx = createHandlerMethod(handler, invocableMethod);
				registerHandlerMethod(handler, invocableMethod, mapping);
			});
		}
	}

	protected HandlerMethod createHandlerMethod(Object handler, Method method) {
		HandlerMethod handlerMethod;
		if (handler instanceof String) {
			String beanName = (String) handler;
			handlerMethod = new HandlerMethod(beanName,
					obtainApplicationContext().getAutowireCapableBeanFactory(), method);
		}
		else {
			handlerMethod = new HandlerMethod(handler, method);
		}
		return handlerMethod;
	}


	protected void registerHandlerMethod(Object handler, Method method, Object mapping) {

	}

	protected Object getMappingForMethod(Method method, Class<?> handlerType) {
		return createRequestMappingInfo(method);
	}

	private Object createRequestMappingInfo(AnnotatedElement element) {
		CoapRequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, CoapRequestMapping.class);
		return requestMapping;
	}


}

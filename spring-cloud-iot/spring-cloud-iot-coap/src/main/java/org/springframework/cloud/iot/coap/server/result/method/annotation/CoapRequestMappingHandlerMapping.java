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

import java.lang.reflect.AnnotatedElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.coap.annotation.CoapController;
import org.springframework.cloud.iot.coap.annotation.CoapRequestMapping;
import org.springframework.cloud.iot.coap.server.HandlerMapping;
import org.springframework.cloud.iot.coap.server.result.method.CoapRequestMappingInfo;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * An implementation of {@link HandlerMapping} that creates
 * {@link CoapRequestMappingInfo} instances from class-level and method-level
 * {@link CoapRequestMapping @CoapRequestMapping} annotations.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapRequestMappingHandlerMapping extends AbstractHandlerMethodMapping {

	private static final Logger log = LoggerFactory.getLogger(CoapRequestMappingHandlerMapping.class);

	@Override
	protected boolean isHandler(Class<?> beanType) {
		return (AnnotatedElementUtils.hasAnnotation(beanType, CoapController.class) ||
				AnnotatedElementUtils.hasAnnotation(beanType, CoapRequestMapping.class));
	}

	@Override
	protected CoapRequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
		CoapRequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, CoapRequestMapping.class);
		return requestMapping != null ? createRequestMappingInfo(requestMapping) : null;
	}

	private CoapRequestMappingInfo createRequestMappingInfo(CoapRequestMapping requestMapping) {
		CoapRequestMappingInfo.Builder builder = CoapRequestMappingInfo
				.paths(requestMapping.path())
				.methods(requestMapping.method());
		return builder.build();
	}

}

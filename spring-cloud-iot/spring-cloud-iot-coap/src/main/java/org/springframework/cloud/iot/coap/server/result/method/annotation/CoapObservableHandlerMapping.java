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
package org.springframework.cloud.iot.coap.server.result.method.annotation;

import java.lang.reflect.AnnotatedElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.coap.annotation.CoapController;
import org.springframework.cloud.iot.coap.annotation.CoapObservable;
import org.springframework.cloud.iot.coap.server.result.method.CoapRequestMappingInfo;
import org.springframework.core.annotation.AnnotatedElementUtils;

public class CoapObservableHandlerMapping extends AbstractHandlerMethodMapping {

	private static final Logger log = LoggerFactory.getLogger(CoapObservableHandlerMapping.class);

	@Override
	protected boolean isHandler(Class<?> beanType) {
		return (AnnotatedElementUtils.hasAnnotation(beanType, CoapController.class) ||
				AnnotatedElementUtils.hasAnnotation(beanType, CoapObservable.class));
	}

	@Override
	protected CoapRequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
		CoapObservable coapObservable = AnnotatedElementUtils.findMergedAnnotation(element, CoapObservable.class);
		return createRequestMappingInfo(coapObservable);
	}

	private CoapRequestMappingInfo createRequestMappingInfo(CoapObservable requestMapping) {
		if (requestMapping == null) {
			return null;
		}
		CoapRequestMappingInfo info = CoapRequestMappingInfo.paths(requestMapping.path()).build();
		return info;
	}
}

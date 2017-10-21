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
package org.springframework.coap.server.result.method.annotation;

import java.lang.reflect.AnnotatedElement;

import org.springframework.coap.annotation.CoapController;
import org.springframework.coap.annotation.CoapRequestMapping;
import org.springframework.coap.server.HandlerMapping;
import org.springframework.coap.server.result.method.CoapRequestMappingInfo;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * An implementation of {@link HandlerMapping} that creates
 * {@link CoapRequestMappingInfo} instances from class-level and method-level
 * {@link CoapRequestMapping @CoapRequestMapping} annotations.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapRequestMappingHandlerMapping extends AbstractHandlerMethodMapping implements EmbeddedValueResolverAware {

	@Nullable
	private StringValueResolver embeddedValueResolver;

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

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.embeddedValueResolver = resolver;
	}

	/**
	 * Resolve placeholder values in the given array of patterns.
	 *
	 * @param patterns the patterns
	 * @return a new array with updated patterns
	 */
	protected String[] resolveEmbeddedValuesInPatterns(String[] patterns) {
		if (this.embeddedValueResolver == null) {
			return patterns;
		}
		else {
			String[] resolvedPatterns = new String[patterns.length];
			for (int i = 0; i < patterns.length; i++) {
				resolvedPatterns[i] = this.embeddedValueResolver.resolveStringValue(patterns[i]);
			}
			return resolvedPatterns;
		}
	}

	private CoapRequestMappingInfo createRequestMappingInfo(CoapRequestMapping requestMapping) {
		CoapRequestMappingInfo.Builder builder = CoapRequestMappingInfo
				.paths(resolveEmbeddedValuesInPatterns(requestMapping.path()))
				.methods(requestMapping.method())
				.headers(requestMapping.headers())
				.consumes(requestMapping.consumes());
		return builder.build();
	}
}

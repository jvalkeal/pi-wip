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

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Set;

import org.junit.Test;
import org.springframework.cloud.iot.coap.CoapMethod;
import org.springframework.cloud.iot.coap.annotation.CoapController;
import org.springframework.cloud.iot.coap.annotation.CoapDeleteMapping;
import org.springframework.cloud.iot.coap.annotation.CoapGetMapping;
import org.springframework.cloud.iot.coap.annotation.CoapPostMapping;
import org.springframework.cloud.iot.coap.annotation.CoapPutMapping;
import org.springframework.cloud.iot.coap.annotation.CoapRequestMapping;
import org.springframework.cloud.iot.coap.server.result.method.CoapRequestMappingInfo;
import org.springframework.cloud.iot.coap.server.support.util.pattern.PathPattern;

/**
 * Unit tests for {@link CoapRequestMappingHandlerMappingTests}.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapRequestMappingHandlerMappingTests {

	private final CoapRequestMappingHandlerMapping handlerMapping = new CoapRequestMappingHandlerMapping();

	@Test
	public void resolveEmbeddedValuesInPatterns() {
		this.handlerMapping.setEmbeddedValueResolver(
				value -> "/${pattern}/bar".equals(value) ? "/foo/bar" : value
		);

		String[] patterns = new String[] { "/foo", "/${pattern}/bar" };
		String[] result = this.handlerMapping.resolveEmbeddedValuesInPatterns(patterns);

		assertThat(new String[] { "/foo", "/foo/bar" }).contains(result);
	}

	@Test
	public void getMapping() throws Exception {
		assertComposedAnnotationMapping(CoapMethod.GET);
	}

	@Test
	public void postMapping() throws Exception {
		assertComposedAnnotationMapping(CoapMethod.POST);
	}

	@Test
	public void putMapping() throws Exception {
		assertComposedAnnotationMapping(CoapMethod.PUT);
	}

	@Test
	public void deleteMapping() throws Exception {
		assertComposedAnnotationMapping(CoapMethod.DELETE);
	}

	private CoapRequestMappingInfo assertComposedAnnotationMapping(CoapMethod requestMethod) throws Exception {
		String methodName = requestMethod.name().toLowerCase();
		String path = "/" + methodName;

		return assertComposedAnnotationMapping(methodName, path, requestMethod);
	}

	private CoapRequestMappingInfo assertComposedAnnotationMapping(String methodName, String path,
			CoapMethod requestMethod) throws Exception {

		Class<?> clazz = ComposedAnnotationController.class;
		Method method = clazz.getMethod(methodName);
		CoapRequestMappingInfo info = this.handlerMapping.getMappingForMethod(method, clazz);

		assertThat(info).isNotNull();

		Set<PathPattern> paths = info.getPatternsCondition().getPatterns();
		assertThat(1).isEqualTo(paths.size());
		assertThat(path).isEqualTo(paths.iterator().next().getPatternString());

		Set<CoapMethod> methods = info.getMethodsCondition().getMethods();
		assertThat(1).isEqualTo(methods.size());
		assertThat(requestMethod).isEqualTo(methods.iterator().next());

		return info;
	}

	@CoapController
	@CoapRequestMapping()
	static class ComposedAnnotationController {

		@CoapRequestMapping
		public void handle() {
		}

		@CoapGetMapping(value = "/get")
		public void get() {
		}

		@CoapPostMapping("/post")
		public void post() {
		}

		@CoapPutMapping("/put")
		public void put() {
		}

		@CoapDeleteMapping("/delete")
		public void delete() {
		}
	}

}

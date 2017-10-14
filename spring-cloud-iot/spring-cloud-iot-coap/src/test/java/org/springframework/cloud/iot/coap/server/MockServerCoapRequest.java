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
package org.springframework.cloud.iot.coap.server;

import org.springframework.cloud.iot.coap.CoapMethod;

public class MockServerCoapRequest {

	public static BaseBuilder<?> get(String urlTemplate, Object... uriVars) {
		return method(CoapMethod.GET, urlTemplate, uriVars);
	}

	public static BodyBuilder method(CoapMethod method, String urlTemplate, Object... vars) {
//		URI url = UriComponentsBuilder.fromUriString(urlTemplate).buildAndExpand(vars).encode().toUri();
		return new DefaultBodyBuilder();
	}

	public interface BaseBuilder<B extends BaseBuilder<B>> {

		MockServerCoapRequest build();
	}

	private static class DefaultBodyBuilder implements BodyBuilder {

		@Override
		public MockServerCoapRequest build() {
			return null;
		}
	}

	public interface BodyBuilder extends BaseBuilder<BodyBuilder> {

	}
}

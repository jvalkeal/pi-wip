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
package org.springframework.cloud.iot.coap.server.result.method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.cloud.iot.coap.CoapMethod;
import org.springframework.cloud.iot.coap.server.CoapRequestCondition;
import org.springframework.cloud.iot.coap.server.ServerCoapExchange;
import org.springframework.cloud.iot.coap.server.result.condition.CoapRequestMethodsRequestCondition;
import org.springframework.lang.Nullable;

/**
 * Encapsulates the following request mapping conditions:
 *
 * @author Janne Valkealahti
 *
 */
public class CoapRequestMappingInfo implements CoapRequestCondition<CoapRequestMappingInfo>{

	private List<String> paths;
	private final CoapRequestMethodsRequestCondition methodsCondition;

	public CoapRequestMappingInfo(List<String> paths) {
		this(paths, null);
	}

	public CoapRequestMappingInfo(List<String> paths, CoapRequestMethodsRequestCondition methods) {
		this.paths = paths;
		this.methodsCondition = (methods != null ? methods : new CoapRequestMethodsRequestCondition());
	}

	@Override
	public CoapRequestMappingInfo combine(CoapRequestMappingInfo other) {
		ArrayList<String> newPaths = new ArrayList<>();
		for (String path1 : other.getPaths()) {
			for (String path2 : getPaths()) {
				newPaths.add(path2 + path1);
			}
		}
		return new CoapRequestMappingInfo(newPaths);
	}

	@Override
	public CoapRequestMappingInfo getMatchingCondition(ServerCoapExchange exchange) {
		return null;
	}

	@Override
	public int compareTo(CoapRequestMappingInfo other, ServerCoapExchange exchange) {
		return 0;
	}

	public List<String> getPaths() {
		return paths;
	}

	/**
	 * Create a new {@code CoapRequestMappingInfo.Builder} with the given paths.
	 * @param paths the paths to use
	 *
	 * @return the builder
	 */
	public static Builder paths(String... paths) {
		return new DefaultBuilder(paths);
	}

	/**
	 * Defines a builder for creating a {@link CoapRequestMappingInfo}.
	 */
	public interface Builder {

		/**
		 * Set the coap request method paths.
		 *
		 * @param paths the paths
		 * @return the builder for chaining
		 */
		Builder paths(String... paths);

		/**
		 * Set the coap request method conditions.
		 *
		 * @param methods the methods
		 * @return the builder for chaining
		 */
		Builder methods(CoapMethod... methods);


		/**
		 * Builds the {@link CoapRequestMappingInfo}.
		 *
		 * @return the coap request mapping info
		 */
		CoapRequestMappingInfo build();
	}

	private static class DefaultBuilder implements Builder {

		private String[] paths;

		@Nullable
		private CoapMethod[] methods;

		public DefaultBuilder(String... paths) {
			this.paths = paths;
		}

		@Override
		public Builder paths(String... paths) {
			this.paths = paths;
			return this;
		}

		@Override
		public Builder methods(CoapMethod... methods) {
			this.methods = methods;
			return this;
		}

		@Override
		public CoapRequestMappingInfo build() {
			return new CoapRequestMappingInfo(Arrays.asList(paths), new CoapRequestMethodsRequestCondition(methods));
		}

	}

}

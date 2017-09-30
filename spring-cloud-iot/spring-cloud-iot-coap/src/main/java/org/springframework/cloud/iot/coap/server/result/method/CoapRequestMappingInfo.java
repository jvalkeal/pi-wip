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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cloud.iot.coap.CoapMethod;
import org.springframework.cloud.iot.coap.server.CoapRequestCondition;
import org.springframework.cloud.iot.coap.server.ServerCoapExchange;
import org.springframework.cloud.iot.coap.server.result.condition.CoapRequestMethodsRequestCondition;
import org.springframework.cloud.iot.coap.server.result.condition.CoapRequestPatternsRequestCondition;
import org.springframework.cloud.iot.coap.server.support.util.pattern.PathPattern;
import org.springframework.cloud.iot.coap.server.support.util.pattern.PathPatternParser;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * Encapsulates the following request mapping conditions:
 *
 * @author Janne Valkealahti
 *
 */
public class CoapRequestMappingInfo implements CoapRequestCondition<CoapRequestMappingInfo>{

	private final CoapRequestPatternsRequestCondition patternsCondition;
	private final CoapRequestMethodsRequestCondition methodsCondition;

	/**
	 * Instantiates a new coap request mapping info.
	 *
	 * @param patterns the patterns
	 * @param methods the methods
	 */
	public CoapRequestMappingInfo(CoapRequestPatternsRequestCondition patterns, CoapRequestMethodsRequestCondition methods) {
		this.patternsCondition = patterns != null ? patterns : new CoapRequestPatternsRequestCondition();
		this.methodsCondition = (methods != null ? methods : new CoapRequestMethodsRequestCondition());
	}

	@Override
	public CoapRequestMappingInfo combine(CoapRequestMappingInfo other) {
		CoapRequestPatternsRequestCondition patterns = this.patternsCondition.combine(other.patternsCondition);
		CoapRequestMethodsRequestCondition methods = this.methodsCondition.combine(other.methodsCondition);
		return new CoapRequestMappingInfo(patterns, methods);
	}

	@Override
	public CoapRequestMappingInfo getMatchingCondition(ServerCoapExchange exchange) {
		CoapRequestMethodsRequestCondition methods = methodsCondition.getMatchingCondition(exchange);
		if (methods == null) {
			return null;
		}
		CoapRequestPatternsRequestCondition patterns = patternsCondition.getMatchingCondition(exchange);
		if (patterns == null) {
			return null;
		}
		return new CoapRequestMappingInfo(patterns, methods);
	}

	@Override
	public int compareTo(CoapRequestMappingInfo other, ServerCoapExchange exchange) {
		return 0;
	}

	public CoapRequestPatternsRequestCondition getPatternsCondition() {
		return patternsCondition;
	}

	public CoapRequestMethodsRequestCondition getMethodsCondition() {
		return methodsCondition;
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
			PathPatternParser parser = new PathPatternParser();
			CoapRequestPatternsRequestCondition patternsCondition = new CoapRequestPatternsRequestCondition(parse(this.paths, parser));
			return new CoapRequestMappingInfo(patternsCondition, new CoapRequestMethodsRequestCondition(methods));
		}

		private static List<PathPattern> parse(String[] paths, PathPatternParser parser) {
			return Arrays.stream(paths).map(path -> {
				if (StringUtils.hasText(path) && !path.startsWith("/")) {
					path = "/" + path;
				}
				return parser.parse(path);
			}).collect(Collectors.toList());
		}
	}
}

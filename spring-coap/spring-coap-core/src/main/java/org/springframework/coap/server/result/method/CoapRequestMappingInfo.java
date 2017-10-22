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
package org.springframework.coap.server.result.method;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.coap.CoapMethod;
import org.springframework.coap.server.CoapRequestCondition;
import org.springframework.coap.server.ServerCoapExchange;
import org.springframework.coap.server.result.condition.CoapConsumesRequestCondition;
import org.springframework.coap.server.result.condition.CoapRequestMethodsRequestCondition;
import org.springframework.coap.server.result.condition.CoapRequestPatternsRequestCondition;
import org.springframework.coap.server.result.condition.CoapHeadersRequestCondition;
import org.springframework.coap.server.result.condition.CoapProducesRequestCondition;
import org.springframework.coap.server.support.util.pattern.PathPattern;
import org.springframework.coap.server.support.util.pattern.PathPatternParser;
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
	private final CoapHeadersRequestCondition headersCondition;
	private final CoapConsumesRequestCondition consumesCondition;
	private final CoapProducesRequestCondition producesCondition;

	/**
	 * Instantiates a new coap request mapping info.
	 *
	 * @param patterns the patterns
	 * @param methods the methods
	 * @param headers the headers
	 * @param consumes the consumes
	 * @param produces the produces
	 */
	public CoapRequestMappingInfo(CoapRequestPatternsRequestCondition patterns,
			CoapRequestMethodsRequestCondition methods, CoapHeadersRequestCondition headers,
			CoapConsumesRequestCondition consumes, CoapProducesRequestCondition produces) {
		this.patternsCondition = patterns != null ? patterns : new CoapRequestPatternsRequestCondition();
		this.methodsCondition = (methods != null ? methods : new CoapRequestMethodsRequestCondition());
		this.headersCondition = (headers != null ? headers : new CoapHeadersRequestCondition());
		this.consumesCondition = (consumes != null ? consumes : new CoapConsumesRequestCondition());
		this.producesCondition = (produces != null ? produces : new CoapProducesRequestCondition());
	}

	@Override
	public CoapRequestMappingInfo combine(CoapRequestMappingInfo other) {
		CoapRequestPatternsRequestCondition patterns = this.patternsCondition.combine(other.patternsCondition);
		CoapRequestMethodsRequestCondition methods = this.methodsCondition.combine(other.methodsCondition);
		CoapHeadersRequestCondition headers = this.headersCondition.combine(other.headersCondition);
		CoapConsumesRequestCondition consumes = this.consumesCondition.combine(other.consumesCondition);
		CoapProducesRequestCondition produces = this.producesCondition.combine(other.producesCondition);
		return new CoapRequestMappingInfo(patterns, methods, headers, consumes, produces);
	}

	@Override
	public CoapRequestMappingInfo getMatchingCondition(ServerCoapExchange exchange) {
		CoapRequestMethodsRequestCondition methods = methodsCondition.getMatchingCondition(exchange);
		CoapHeadersRequestCondition headers = this.headersCondition.getMatchingCondition(exchange);
		CoapConsumesRequestCondition consumes = this.consumesCondition.getMatchingCondition(exchange);
		CoapProducesRequestCondition produces = this.producesCondition.getMatchingCondition(exchange);
		if (methods == null || headers == null || consumes == null  || produces == null) {
			return null;
		}
		CoapRequestPatternsRequestCondition patterns = patternsCondition.getMatchingCondition(exchange);
		if (patterns == null) {
			return null;
		}
		return new CoapRequestMappingInfo(patterns, methods, headers, consumes, produces);
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

	public CoapHeadersRequestCondition getHeadersCondition() {
		return headersCondition;
	}

	public CoapConsumesRequestCondition getConsumesCondition() {
		return consumesCondition;
	}

	public CoapProducesRequestCondition getProducesCondition() {
		return producesCondition;
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
		 * Set the header conditions.
		 * <p>By default this is not set.
		 *
		 * @param headers the headers
		 * @return the builder for chaining
		 */
		Builder headers(String... headers);

		/**
		 * Set the consumes conditions.
		 *
		 * @param consumes the consumes
		 * @return the builder for chaining
		 */
		Builder consumes(String... consumes);

		/**
		 * Set the produces conditions.
		 *
		 * @param produces the produces
		 * @return the builder for chaining
		 */
		Builder produces(String... produces);

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

		@Nullable
		private String[] headers;

		@Nullable
		private String[] consumes;

		@Nullable
		private String[] produces;

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
		public DefaultBuilder headers(String... headers) {
			this.headers = headers;
			return this;
		}

		@Override
		public DefaultBuilder consumes(String... consumes) {
			this.consumes = consumes;
			return this;
		}

		@Override
		public DefaultBuilder produces(String... produces) {
			this.produces = produces;
			return this;
		}

		@Override
		public CoapRequestMappingInfo build() {
			PathPatternParser parser = new PathPatternParser();
			CoapRequestPatternsRequestCondition patternsCondition = new CoapRequestPatternsRequestCondition(parse(this.paths, parser));
			return new CoapRequestMappingInfo(patternsCondition, new CoapRequestMethodsRequestCondition(methods),
					new CoapHeadersRequestCondition(headers), new CoapConsumesRequestCondition(consumes),
					new CoapProducesRequestCondition(produces));
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

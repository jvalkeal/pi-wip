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
package org.springframework.coap.server.result.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.coap.InvalidMediaTypeException;
import org.springframework.coap.MediaType;
import org.springframework.coap.annotation.CoapRequestMapping;
import org.springframework.coap.server.ServerCoapExchange;
import org.springframework.util.StringUtils;

/**
 * A logical disjunction (' || ') request condition to match a request's
 * {@code Content-Format} header to a list of media type expressions. Two kinds
 * of media type expressions are supported, which are described in
 * {@link CoapRequestMapping#consumes()} and {@link CoapRequestMapping#headers()} where
 * the header name is {@code Content-Format}. Regardless of which syntax is
 * used, the semantics are the same.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapConsumesRequestCondition extends AbstractRequestCondition<CoapConsumesRequestCondition> {

	private final List<ConsumeMediaTypeExpression> expressions;

	public CoapConsumesRequestCondition(String... consumes) {
		this(consumes, null);
	}

	public CoapConsumesRequestCondition(String[] consumes, String[] headers) {
		this(parseExpressions(consumes, headers));
	}

	private CoapConsumesRequestCondition(Collection<ConsumeMediaTypeExpression> expressions) {
		this.expressions = new ArrayList<>(expressions);
		Collections.sort(this.expressions);
	}

	@Override
	public CoapConsumesRequestCondition combine(CoapConsumesRequestCondition other) {
		return !other.expressions.isEmpty() ? other : this;
	}

	@Override
	public CoapConsumesRequestCondition getMatchingCondition(ServerCoapExchange exchange) {
		if (isEmpty()) {
			return this;
		}

		Set<ConsumeMediaTypeExpression> result = new LinkedHashSet<>(expressions);
		for (Iterator<ConsumeMediaTypeExpression> iterator = result.iterator(); iterator.hasNext();) {
			ConsumeMediaTypeExpression expression = iterator.next();
			if (!expression.match(exchange)) {
				iterator.remove();
			}
		}
		return (result.isEmpty()) ? null : new CoapConsumesRequestCondition(result);
	}

	@Override
	public int compareTo(CoapConsumesRequestCondition other, ServerCoapExchange exchange) {
		if (this.expressions.isEmpty() && other.expressions.isEmpty()) {
			return 0;
		}
		else if (this.expressions.isEmpty()) {
			return 1;
		}
		else if (other.expressions.isEmpty()) {
			return -1;
		}
		else {
			return this.expressions.get(0).compareTo(other.expressions.get(0));
		}
	}

	@Override
	protected Collection<?> getContent() {
		return this.expressions;
	}

	@Override
	protected String getToStringInfix() {
		return " || ";
	}

	@Override
	public boolean isEmpty() {
		return this.expressions.isEmpty();
	}

	public Set<MediaType> getConsumableMediaTypes() {
		Set<MediaType> result = new LinkedHashSet<>();
		for (ConsumeMediaTypeExpression expression : this.expressions) {
			if (!expression.isNegated()) {
				result.add(expression.getMediaType());
			}
		}
		return result;
	}

	public Set<MediaTypeExpression> getExpressions() {
		return new LinkedHashSet<>(this.expressions);
	}


	private static Set<ConsumeMediaTypeExpression> parseExpressions(String[] consumes, String[] headers) {
		Set<ConsumeMediaTypeExpression> result = new LinkedHashSet<>();
		if (headers != null) {
			for (String header : headers) {
				CoapHeadersRequestCondition.HeaderExpression expr = new CoapHeadersRequestCondition.HeaderExpression(header);
				if ("Content-Type".equalsIgnoreCase(expr.name)) {
					for (MediaType mediaType : MediaType.parseMediaTypes(expr.value)) {
						result.add(new ConsumeMediaTypeExpression(mediaType, expr.isNegated));
					}
				}
			}
		}
		if (consumes != null) {
			for (String consume : consumes) {
				result.add(new ConsumeMediaTypeExpression(consume));
			}
		}
		return result;
	}

	static class ConsumeMediaTypeExpression extends AbstractMediaTypeExpression {

		ConsumeMediaTypeExpression(String expression) {
			super(expression);
		}

		ConsumeMediaTypeExpression(MediaType mediaType, boolean negated) {
			super(mediaType, negated);
		}

		public final boolean match(MediaType contentType) {
			boolean match = getMediaType().includes(contentType);
			return (!isNegated() ? match : !match);
		}

		@Override
		protected boolean matchMediaType(ServerCoapExchange exchange) throws RuntimeException {
			try {
				MediaType contentFormat = exchange.getRequest().getHeaders().getContentFormat();
				contentFormat = (contentFormat != null ? contentFormat : MediaType.TEXT_PLAIN);
				return getMediaType().includes(contentFormat);
			}
			catch (InvalidMediaTypeException ex) {
				throw new RuntimeException(ex);
//				throw new UnsupportedMediaTypeStatusException("Can't parse Content-Type [" +
//						exchange.getRequest().getHeaders().getFirst("Content-Type") +
//						"]: " + ex.getMessage());
			}
		}

	}
}

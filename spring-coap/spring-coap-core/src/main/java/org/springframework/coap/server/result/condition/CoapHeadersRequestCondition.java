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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.coap.annotation.CoapRequestMapping;
import org.springframework.coap.server.ServerCoapExchange;
import org.springframework.lang.Nullable;

public final class CoapHeadersRequestCondition extends AbstractRequestCondition<CoapHeadersRequestCondition> {

	private final Set<HeaderExpression> expressions;


	/**
	 * Create a new instance from the given header expressions. Expressions with
	 * header names 'Accept' or 'Content-Type' are ignored. See {@link CoapConsumesRequestCondition}
	 * and CoapProducesRequestCondition for those.
	 * @param headers media type expressions with syntax defined in {@link CoapRequestMapping#headers()};
	 * if 0, the condition will match to every request
	 */
	public CoapHeadersRequestCondition(String... headers) {
		this(parseExpressions(headers));
	}

	private CoapHeadersRequestCondition(Collection<HeaderExpression> conditions) {
		this.expressions = Collections.unmodifiableSet(new LinkedHashSet<>(conditions));
	}


	private static Collection<HeaderExpression> parseExpressions(String... headers) {
		Set<HeaderExpression> expressions = new LinkedHashSet<>();
		if (headers != null) {
			for (String header : headers) {
				HeaderExpression expr = new HeaderExpression(header);
				if ("Accept".equalsIgnoreCase(expr.name) || "Content-Type".equalsIgnoreCase(expr.name)) {
					continue;
				}
				expressions.add(expr);
			}
		}
		return expressions;
	}

	/**
	 * Return the contained request header expressions.
	 *
	 * @return the expressions
	 */
	public Set<NameValueExpression<String>> getExpressions() {
		return new LinkedHashSet<>(this.expressions);
	}

	@Override
	protected Collection<HeaderExpression> getContent() {
		return this.expressions;
	}

	@Override
	protected String getToStringInfix() {
		return " && ";
	}

	/**
	 * Returns a new instance with the union of the header expressions
	 * from "this" and the "other" instance.
	 *
	 * @return the combined condition
	 */
	@Override
	public CoapHeadersRequestCondition combine(CoapHeadersRequestCondition other) {
		Set<HeaderExpression> set = new LinkedHashSet<>(this.expressions);
		set.addAll(other.expressions);
		return new CoapHeadersRequestCondition(set);
	}

	/**
	 * Returns "this" instance if the request matches all expressions;
	 * or {@code null} otherwise.
	 *
	 * @return the matching condition
	 */
	@Override
	@Nullable
	public CoapHeadersRequestCondition getMatchingCondition(ServerCoapExchange exchange) {
		for (HeaderExpression expression : expressions) {
			if (!expression.match(exchange)) {
				return null;
			}
		}
		return this;
	}

	/**
	 * Returns:
	 * <ul>
	 * <li>0 if the two conditions have the same number of header expressions
	 * <li>Less than 0 if "this" instance has more header expressions
	 * <li>Greater than 0 if the "other" instance has more header expressions
	 * </ul>
	 * <p>It is assumed that both instances have been obtained via
	 * {@link #getMatchingCondition(ServerCoapExchange)} and each instance
	 * contains the matching header expression only or is otherwise empty.
	 *
	 * @return the compare value
	 */
	@Override
	public int compareTo(CoapHeadersRequestCondition other, ServerCoapExchange exchange) {
		return other.expressions.size() - this.expressions.size();
	}


	/**
	 * Parses and matches a single header expression to a request.
	 */
	static class HeaderExpression extends AbstractNameValueExpression<String> {

		public HeaderExpression(String expression) {
			super(expression);
		}

		@Override
		protected boolean isCaseSensitiveName() {
			return false;
		}

		@Override
		protected String parseValue(String valueExpression) {
			return valueExpression;
		}

		@Override
		protected boolean matchName(ServerCoapExchange exchange) {
			return (exchange.getRequest().getHeaders().get(this.name) != null);
		}

		@Override
		protected boolean matchValue(ServerCoapExchange exchange) {
//			return (this.value != null && this.value.equals(exchange.getRequest().getHeaders().getFirst(this.name)));
			return (this.value != null && this.value.equals(exchange.getRequest().getHeaders().get(this.name)));
		}
	}

}

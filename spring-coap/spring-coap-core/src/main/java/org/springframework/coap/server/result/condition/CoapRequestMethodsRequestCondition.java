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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.coap.CoapMethod;
import org.springframework.coap.server.ServerCoapExchange;
import org.springframework.lang.Nullable;

public class CoapRequestMethodsRequestCondition extends AbstractRequestCondition<CoapRequestMethodsRequestCondition> {

	private final Set<CoapMethod> methods;

	/**
	 * Create a new instance with the given request methods.
	 * @param requestMethods 0 or more HTTP request methods;
	 * if, 0 the condition will match to every request
	 */
	public CoapRequestMethodsRequestCondition(CoapMethod... requestMethods) {
		this(asList(requestMethods));
	}

	private CoapRequestMethodsRequestCondition(Collection<CoapMethod> requestMethods) {
		this.methods = Collections.unmodifiableSet(new LinkedHashSet<>(requestMethods));
	}

	private static List<CoapMethod> asList(CoapMethod... requestMethods) {
		return (requestMethods != null ? Arrays.asList(requestMethods) : Collections.emptyList());
	}

	/**
	 * Returns all {@link CoapMethod}s contained in this condition.
	 *
	 * @return the methods
	 */
	public Set<CoapMethod> getMethods() {
		return this.methods;
	}

	@Override
	protected Collection<CoapMethod> getContent() {
		return this.methods;
	}

	@Override
	protected String getToStringInfix() {
		return " || ";
	}

	/**
	 * Returns a new instance with a union of the HTTP request methods
	 * from "this" and the "other" instance.
	 */
	@Override
	public CoapRequestMethodsRequestCondition combine(CoapRequestMethodsRequestCondition other) {
		Set<CoapMethod> set = new LinkedHashSet<>(this.methods);
		set.addAll(other.methods);
		return new CoapRequestMethodsRequestCondition(set);
	}

	/**
	 * Check if any of the HTTP request methods match the given request and
	 * return an instance that contains the matching HTTP request method only.
	 * @param exchange the current exchange
	 * @return the same instance if the condition is empty (unless the request
	 * method is HTTP OPTIONS), a new condition with the matched request method,
	 * or {@code null} if there is no match or the condition is empty and the
	 * request method is OPTIONS.
	 */
	@Override
	@Nullable
	public CoapRequestMethodsRequestCondition getMatchingCondition(ServerCoapExchange exchange) {
		if (getMethods().isEmpty()) {
			return this;
		}
		return matchRequestMethod(exchange.getRequest().getMethod());
	}

	@Nullable
	private CoapRequestMethodsRequestCondition matchRequestMethod(@Nullable CoapMethod coapMethod) {
		if (coapMethod != null) {
			for (CoapMethod method : getMethods()) {
				if (coapMethod.matches(method.name())) {
					return new CoapRequestMethodsRequestCondition(method);
				}
			}
		}
		return null;
	}

	/**
	 * Returns:
	 * <ul>
	 * <li>0 if the two conditions contain the same number of HTTP request methods
	 * <li>Less than 0 if "this" instance has an COAP request method but "other" doesn't
	 * <li>Greater than 0 "other" has an COAP request method but "this" doesn't
	 * </ul>
	 * <p>It is assumed that both instances have been obtained via
	 * {@link #getMatchingCondition(ServerCoapExchange)} and therefore each instance
	 * contains the matching HTTP request method only or is otherwise empty.
	 */
	@Override
	public int compareTo(CoapRequestMethodsRequestCondition other, ServerCoapExchange exchange) {
		return (other.methods.size() - this.methods.size());
	}

}

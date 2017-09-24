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
package org.springframework.cloud.iot.coap.server.result.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.cloud.iot.coap.server.ServerCoapExchange;
import org.springframework.cloud.iot.coap.server.support.PathContainer;
import org.springframework.cloud.iot.coap.server.support.util.pattern.PathPattern;
import org.springframework.lang.Nullable;

public class CoapRequestPatternsRequestCondition extends AbstractRequestCondition<CoapRequestPatternsRequestCondition> {

	private final SortedSet<PathPattern> patterns;


	/**
	 * Creates a new instance with the given URL patterns.
	 * @param patterns 0 or more URL patterns; if 0 the condition will match to every request.
	 */
	public CoapRequestPatternsRequestCondition(PathPattern... patterns) {
		this(Arrays.asList(patterns));
	}

	/**
	 * Creates a new instance with the given {@code Stream} of URL patterns.
	 *
	 * @param patterns the path patterns
	 */
	public CoapRequestPatternsRequestCondition(List<PathPattern> patterns) {
		this(toSortedSet(patterns));
	}

	private static SortedSet<PathPattern> toSortedSet(Collection<PathPattern> patterns) {
		TreeSet<PathPattern> sorted = new TreeSet<>();
		sorted.addAll(patterns);
		return sorted;
	}

	private CoapRequestPatternsRequestCondition(SortedSet<PathPattern> patterns) {
		this.patterns = patterns;
	}

	public Set<PathPattern> getPatterns() {
		return this.patterns;
	}

	@Override
	protected Collection<PathPattern> getContent() {
		return this.patterns;
	}

	@Override
	protected String getToStringInfix() {
		return " || ";
	}

	/**
	 * Returns a new instance with URL patterns from the current instance ("this") and
	 * the "other" instance as follows:
	 * <ul>
	 * <li>If there are patterns in both instances, combine the patterns in "this" with
	 * the patterns in "other" using {@link PathPattern#combine(PathPattern)}.
	 * <li>If only one instance has patterns, use them.
	 * <li>If neither instance has patterns, use an empty String (i.e. "").
	 * </ul>
	 */
	@Override
	public CoapRequestPatternsRequestCondition combine(CoapRequestPatternsRequestCondition other) {
		List<PathPattern> combined = new ArrayList<>();
		if (!this.patterns.isEmpty() && !other.patterns.isEmpty()) {
			for (PathPattern pattern1 : this.patterns) {
				for (PathPattern pattern2 : other.patterns) {
					combined.add(pattern1.combine(pattern2));
				}
			}
		}
		else if (!this.patterns.isEmpty()) {
			combined.addAll(this.patterns);
		}
		else if (!other.patterns.isEmpty()) {
			combined.addAll(other.patterns);
		}
		return new CoapRequestPatternsRequestCondition(combined);
	}

	/**
	 * Checks if any of the patterns match the given request and returns an instance
	 * that is guaranteed to contain matching patterns, sorted.
	 * @param exchange the current exchange
	 * @return the same instance if the condition contains no patterns;
	 * or a new condition with sorted matching patterns;
	 * or {@code null} if no patterns match.
	 */
	@Override
	@Nullable
	public CoapRequestPatternsRequestCondition getMatchingCondition(ServerCoapExchange exchange) {
		if (this.patterns.isEmpty()) {
			return this;
		}
		SortedSet<PathPattern> matches = getMatchingPatterns(exchange);
		return matches.isEmpty() ? null :
				new CoapRequestPatternsRequestCondition(matches);
	}

	/**
	 * Find the patterns matching the given lookup path. Invoking this method should
	 * yield results equivalent to those of calling
	 * {@link #getMatchingCondition(ServerWebExchange)}.
	 * This method is provided as an alternative to be used if no request is available
	 * (e.g. introspection, tooling, etc).
	 * @param exchange the current exchange
	 * @return a sorted set of matching patterns sorted with the closest match first
	 */
	private SortedSet<PathPattern> getMatchingPatterns(ServerCoapExchange exchange) {
		PathContainer lookupPath = exchange.getRequest().getPath().pathWithinApplication();
		return patterns.stream()
				.filter(pattern -> pattern.matches(lookupPath))
				.collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Compare the two conditions based on the URL patterns they contain.
	 * Patterns are compared one at a time, from top to bottom. If all compared
	 * patterns match equally, but one instance has more patterns, it is
	 * considered a closer match.
	 * <p>It is assumed that both instances have been obtained via
	 * {@link #getMatchingCondition(ServerCoapExchange)} to ensure they
	 * contain only patterns that match the request and are sorted with
	 * the best matches on top.
	 */
	@Override
	public int compareTo(CoapRequestPatternsRequestCondition other, ServerCoapExchange exchange) {
		Iterator<PathPattern> iterator = this.patterns.iterator();
		Iterator<PathPattern> iteratorOther = other.getPatterns().iterator();
		while (iterator.hasNext() && iteratorOther.hasNext()) {
			int result = PathPattern.SPECIFICITY_COMPARATOR.compare(iterator.next(), iteratorOther.next());
			if (result != 0) {
				return result;
			}
		}
		if (iterator.hasNext()) {
			return -1;
		}
		else if (iteratorOther.hasNext()) {
			return 1;
		}
		else {
			return 0;
		}
	}

}

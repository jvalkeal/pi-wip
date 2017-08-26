package org.springframework.cloud.iot.coap.server;

import org.springframework.lang.Nullable;

/**
 * Contract for request mapping conditions.
 *
 * <p>Request conditions can be combined via {@link #combine(Object)}, matched to
 * a request via {@link #getMatchingCondition(ServerCoapExchange)}, and compared
 * to each other via {@link #compareTo(Object, ServerCoapExchange)} to determine
 * which is a closer match for a given request.
 *
 * @param <T> the type of objects that this RequestCondition can be combined
 * with and compared to
 *
 * @author Janne Valkealahti
 */
public interface RequestCondition<T> {

	/**
	 * Combine this condition with another such as conditions from a
	 * type-level and method-level {@code @RequestMapping} annotation.
	 *
	 * @param other the condition to combine with.
	 * @return a request condition instance that is the result of combining
	 * the two condition instances.
	 */
	T combine(T other);

	/**
	 * Check if the condition matches the request returning a potentially new
	 * instance created for the current request. For example a condition with
	 * multiple URL patterns may return a new instance only with those patterns
	 * that match the request.
	 * <p>For CORS pre-flight requests, conditions should match to the would-be,
	 * actual request (e.g. URL pattern, query parameters, and the HTTP method
	 * from the "Access-Control-Request-Method" header). If a condition cannot
	 * be matched to a pre-flight request it should return an instance with
	 * empty content thus not causing a failure to match.
	 *
	 * @param exchange the exchange
	 * @return a condition instance in case of a match or {@code null} otherwise.
	 */
	@Nullable
	T getMatchingCondition(ServerCoapExchange exchange);

	/**
	 * Compare this condition to another condition in the context of
	 * a specific request. This method assumes both instances have
	 * been obtained via {@link #getMatchingCondition(ServerCoapExchange)}
	 * to ensure they have content relevant to current request only.
	 *
	 * @param other the other condition to compare with
	 * @param exchange the other exchange to compare with
	 * @return the comparison value
	 */
	int compareTo(T other, ServerCoapExchange exchange);
}

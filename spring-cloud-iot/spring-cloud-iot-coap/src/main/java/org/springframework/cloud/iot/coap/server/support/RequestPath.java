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
package org.springframework.cloud.iot.coap.server.support;

import java.net.URI;

import org.springframework.lang.Nullable;

/**
 * Represents the complete path for a request.
 *
 * @author Rossen Stoyanchev
 */
public interface RequestPath extends PathContainer {

	/**
	 * Returns the portion of the URL path that represents the application.
	 * The context path is always at the beginning of the path and starts but
	 * does not end with "/". It is shared for URLs of the same application.
	 * <p>The context path may come from the underlying runtime API such as
	 * when deploying as a WAR to a Servlet container or it may be assigned in
	 * a WebFlux application through the use of
	 * {@link org.springframework.http.server.reactive.ContextPathCompositeHandler
	 * ContextPathCompositeHandler}.
	 *
	 * @return the path container
	 */
	PathContainer contextPath();

	/**
	 * The portion of the request path after the context path.
	 *
	 * @return the path container
	 */
	PathContainer pathWithinApplication();

	/**
	 * Return a new {@code RequestPath} instance with a modified context path.
	 * The new context path must match 0 or more path segments at the start.
	 *
	 * @param contextPath the new context path
	 * @return a new {@code RequestPath} instance
	 */
	RequestPath modifyContextPath(String contextPath);


	/**
	 * Create a new {@code RequestPath} with the given parameters.
	 *
	 * @param uri the uri
	 * @param contextPath the context path
	 * @return the request path
	 */
	static RequestPath parse(URI uri, @Nullable String contextPath) {
		return new DefaultRequestPath(uri, contextPath);
	}

}

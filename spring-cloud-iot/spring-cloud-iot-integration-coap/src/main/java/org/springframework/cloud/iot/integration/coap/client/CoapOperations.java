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
package org.springframework.cloud.iot.integration.coap.client;

import java.net.URI;

import reactor.core.publisher.Flux;

/**
 * Interface specifying a basic set of COAPful operations.
 * Implemented by {@link CoapTemplate}.
 *
 * @author Janne Valkealahti
 * @see CoapTemplate
 */
public interface CoapOperations {

	/**
	 * Retrieve a representation by doing a GET on the specified URL.
	 * The response (if any) is converted and returned.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 *
	 * @param url the URL
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand the template
	 * @param <T> the type of returned object
	 * @return the converted object
	 */
	<T> T getForObject(String url, Class<T> responseType, Object... uriVariables);

	/**
	 * Retrieve a representation by doing a GET on the URL .
	 * The response (if any) is converted and returned.
	 *
	 * @param url the URL
	 * @param responseType the type of the return value
	 * @param <T> the type of returned object
	 * @return the converted object
	 */
	<T> T getForObject(URI url, Class<T> responseType);

	/**
	 * Create a new resource by POSTing the given object to the URI template,
	 * and returns the representation found in the response.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 *
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand the template
	 * @param <T> the type of returned object
	 * @return the converted object
	 */
	<T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables);

	/**
	 * Create a new resource by POSTing the given object to the URL,
	 * and returns the representation found in the response.
	 *
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @param responseType the type of the return value
	 * @param <T> the type of returned object
	 * @return the converted object
	 */
	<T> T postForObject(URI url, Object request, Class<T> responseType);

	/**
	 * Create a new subscription by sending observe request to the URL,
	 * and returns a {@link Flux} of objects.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 *
	 * @param url the URL
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand the template
	 * @param <T> the type of returned object
	 * @return the flux of converted objects
	 */
	<T> Flux<T> observeForObject(String url, Class<T> responseType, Object... uriVariables);

	/**
	 * Create a new subscription by sending observe request to the URL,
	 * and returns a {@link Flux} of objects.
	 *
	 * @param url the URL
	 * @param responseType the type of the return value
	 * @param <T> the type of returned object
	 * @return the flux of converted objects
	 */
	<T> Flux<T> observeForObject(URI url, Class<T> responseType);
}

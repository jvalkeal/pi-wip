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
package org.springframework.coap.client;

import org.springframework.coap.californium.CoapTemplate;

/**
 * Generic callback interface used by {@link CoapTemplate}'s retrieval methods
 * Implementations of this interface perform the actual work of extracting data
 * from a {@link ClientCoapResponse}, but don't need to worry about exception
 * handling or closing resources.
 *
 * @author Janne Valkealahti
 *
 * @param <T> the response type
 */
@FunctionalInterface
public interface ResponseExtractor<T> {

	/**
	 * Extract data from the given {@code ClientCoapResponse} and return it.
	 *
	 * @param response the COAP response
	 * @return the extracted data
	 */
	T extractData(ClientCoapResponse response);
}

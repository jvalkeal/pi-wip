/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.coap.server;

import reactor.core.publisher.Mono;

/**
 * Contract to handle a coap request. This happens in a contract of a
 * {@link ServerCoapExchange} which gives access to both request and response
 * sides.
 *
 * @author Janne Valkealahti
 */
public interface CoapHandler {

	/**
	 * Handle the web server exchange.
	 *
	 * @param exchange the current server exchange
	 * @return {@code Mono<Void>} to indicate when request handling is complete
	 */
	Mono<Void> handle(ServerCoapExchange exchange);
}

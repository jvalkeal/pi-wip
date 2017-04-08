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
package org.springframework.cloud.iot.coap.server;

/**
 * Interface for implementations able to handle coap server requests.
 *
 * @author Janne Valkealahti
 *
 */
@FunctionalInterface
public interface CoapServerHandler {

	/**
	 * Handles a {@link ServerCoapRequest} and returns a {@link ServerCoapResponse}.
	 *
	 * @param request the server coap request
	 * @return the server coap response
	 */
	ServerCoapResponse handle(ServerCoapRequest request);
}

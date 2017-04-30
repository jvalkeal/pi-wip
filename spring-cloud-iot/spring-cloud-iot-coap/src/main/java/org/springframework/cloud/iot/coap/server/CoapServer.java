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

import java.util.Map;

/**
 * Simple interface that represents a fully configured coap server (for example
 * Californium). Allows the server to be {@link #start() started} and
 * {@link #stop() stopped}.
 *
 * @author jvalkealahti
 *
 */
public interface CoapServer {

	/**
	 * Starts the coap server. Calling this method on an already started server
	 * has no effect.
	 *
	 * @throws CoapServerException if the server cannot be started
	 */
	void start() throws CoapServerException;

	/**
	 * Stops the coap server. Calling this method on an already stopped server
	 * has no effect.
	 *
	 * @throws CoapServerException if the server cannot be stopped
	 */
	void stop() throws CoapServerException;

	/**
	 * Return the port this server is listening on.
	 *
	 * @return the port (or -1 if none)
	 */
	int getPort();

	/**
	 * Adds the handler mappings.
	 *
	 * @param mappings the mappings
	 */
	void addHandlerMappings(Map<String, CoapServerHandler> mappings);
}

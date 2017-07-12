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

import java.util.Map;

import org.springframework.cloud.iot.coap.server.CoapHandler;
import org.springframework.cloud.iot.coap.server.CoapServerFactory;

/**
 * A configurable {@link CoapServerFactory}.
 *
 * @author Janne Valkealahti
 *
 */
public interface ConfigurableCoapServerFactory extends CoapServerFactory {

	/**
	 * Sets the port that the coap server should listen on. If not specified
	 * port '5683' will be used. Use port -1 to disable auto-start.
	 *
	 * @param port the port to set
	 */
	void setPort(int port);

	/**
	 * Sets the handler mappings.
	 *
	 * @param mappings the mappings
	 */
	void setHandlerMappings(Map<String, CoapHandler> mappings);
}

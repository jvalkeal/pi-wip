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

import org.springframework.context.ApplicationContext;

/**
 * Factory interface that can be used to create a {@link CoapServer}.
 *
 * @author Janne Valkealahti
 * @see CoapServer
 *
 */
@FunctionalInterface
public interface CoapServerFactory {

	/**
	 * Gets a new fully configured but paused {@link CoapServer} instance.
	 * Clients should not be able to connect to the returned server until
	 * {@link CoapServer#start()} is called (which happens when the
	 * {@link ApplicationContext} has been fully refreshed).
	 *
	 * @return a fully configured {@link CoapServer}
	 */
	CoapServer getCoapServer();
}

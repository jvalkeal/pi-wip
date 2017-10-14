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

/**
 * Contract for a COAP request-response interaction. Provides access to the COAP
 * request and response and also exposes additional server-side processing
 * related properties and features such as request attributes.
 *
 * @author Janne Valkealahti
 *
 */
public interface ServerCoapExchange {

	/**
	 * Return the current COAP request.
	 *
	 * @return the current request
	 */
	ServerCoapRequest getRequest();

	/**
	 * Return the current COAP response.
	 *
	 * @return the current response
	 */
	ServerCoapResponse getResponse();

	/**
	 * Return the current COAP observable context.
	 *
	 * @return the current observable context
	 */
	ServerCoapObservableContext getObservableContext();
}

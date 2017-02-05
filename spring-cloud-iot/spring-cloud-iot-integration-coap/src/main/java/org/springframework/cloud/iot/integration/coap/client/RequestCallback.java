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

/**
 * Callback interface for code that operates on a {@link ClientCoapRequest}. Allows
 * to manipulate the request.
 *
 * <p>Used internally by the {@link CoapTemplate}, but also useful for application code.
 *
 * @author Janne Valkealahti
 * @see CoapTemplate#execute
 */
public interface RequestCallback {

	/**
	 * Gets called by {@link CoapTemplate#execute} with an opened {@code ClientCoapRequest}.
	 * Does not need to care about closing the request or about handling errors:
	 * this will all be handled by the {@code CoapTemplate}.
	 *
	 * @param request the active COAP request
	 */
	void doWithRequest(ClientCoapRequest request);
}

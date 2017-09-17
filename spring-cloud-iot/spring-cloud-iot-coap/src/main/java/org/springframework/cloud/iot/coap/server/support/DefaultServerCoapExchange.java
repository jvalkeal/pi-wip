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
package org.springframework.cloud.iot.coap.server.support;

import org.springframework.cloud.iot.coap.server.ServerCoapExchange;
import org.springframework.cloud.iot.coap.server.ServerCoapObservableContext;
import org.springframework.cloud.iot.coap.server.ServerCoapRequest;
import org.springframework.cloud.iot.coap.server.ServerCoapResponse;

public class DefaultServerCoapExchange implements ServerCoapExchange {

	private final ServerCoapRequest request;
	private final ServerCoapResponse response;
	private ServerCoapObservableContext context;

	public DefaultServerCoapExchange(ServerCoapRequest request, ServerCoapResponse response) {
		this.request = request;
		this.response = response;
	}

	public DefaultServerCoapExchange(ServerCoapRequest request, ServerCoapResponse response,
			ServerCoapObservableContext context) {
		this.request = request;
		this.response = response;
		this.context = context;
	}

	@Override
	public ServerCoapRequest getRequest() {
		return request;
	}

	@Override
	public ServerCoapResponse getResponse() {
		return response;
	}

	@Override
	public ServerCoapObservableContext getObservableContext() {
		return context;
	}
}

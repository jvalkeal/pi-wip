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
package org.springframework.cloud.iot.coap.californium;

import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.springframework.cloud.iot.coap.CoapHeaders;
import org.springframework.cloud.iot.coap.CoapMethod;
import org.springframework.cloud.iot.coap.server.CoapServerHandler;
import org.springframework.cloud.iot.coap.server.ServerCoapResponse;
import org.springframework.cloud.iot.coap.server.support.GenericServerCoapRequest;
import org.springframework.util.CollectionUtils;

/**
 * Californium specific {@link Resource} handling request/response logic via
 * {@link CoapServerHandler}.
 *
 * @author Janne Valkealahti
 *
 */
public class CaliforniumCoapServerHandlerResource extends AbstractCoapResource {

	private List<CoapMethod> allowedMethods = null;
	private final CoapServerHandler handler;

	public CaliforniumCoapServerHandlerResource(String name, CoapServerHandler handler) {
		super(name);
		this.handler = handler;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		if (isMethodAllowed(exchange)) {
			handleRequest(this, exchange);
		} else {
			super.handleGET(exchange);
		}
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		if (isMethodAllowed(exchange)) {
			handleRequest(this, exchange);
		} else {
			super.handlePOST(exchange);
		}
	}

	@Override
	public void handlePUT(CoapExchange exchange) {
		if (isMethodAllowed(exchange)) {
			handleRequest(this, exchange);
		} else {
			super.handlePUT(exchange);
		}
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		if (isMethodAllowed(exchange)) {
			handleRequest(this, exchange);
		} else {
			super.handleDELETE(exchange);
		}
	}

	public void setAllowedMethods(List<CoapMethod> allowedMethods) {
		this.allowedMethods = allowedMethods;
	}

	private void handleRequest(CoapResource resource, CoapExchange exchange) {
		CoapHeaders coapHeaders = new CoapHeaders();
		List<Option> others = exchange.getRequestOptions().getOthers();
		for (Option option : others) {
			coapHeaders.add(option.getNumber(), option.getStringValue().getBytes());
		}

		GenericServerCoapRequest request = new GenericServerCoapRequest(exchange.getRequestPayload(), coapHeaders);
		request.setMethod(CoapMethod.resolve(exchange.getRequestCode().name()));
		request.setContentFormat(exchange.getRequestOptions().getContentFormat());
		ServerCoapResponse response = handler.handle(request);
		ResponseCode responseCode = response.getStatus() != null ? ResponseCode.valueOf(response.getStatus().value) : ResponseCode.CREATED;
		exchange.respond(responseCode, response.getBody());
	}

	private boolean isMethodAllowed(CoapExchange exchange) {
		if (allowedMethods == null) {
			return true;
		} else {
			return CollectionUtils.containsInstance(allowedMethods, CoapMethod.resolve(exchange.getRequestCode().name()));
		}
	}
}

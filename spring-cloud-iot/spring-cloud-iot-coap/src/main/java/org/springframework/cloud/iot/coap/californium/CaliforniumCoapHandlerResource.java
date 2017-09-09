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
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.coap.CoapHeaders;
import org.springframework.cloud.iot.coap.CoapMethod;
import org.springframework.cloud.iot.coap.server.CoapHandler;
import org.springframework.cloud.iot.coap.server.ServerCoapExchange;
import org.springframework.cloud.iot.coap.server.ServerCoapResponse;
import org.springframework.cloud.iot.coap.server.support.DefaultServerCoapExchange;
import org.springframework.cloud.iot.coap.server.support.GenericServerCoapRequest;
import org.springframework.cloud.iot.coap.server.support.GenericServerCoapResponse;
import org.springframework.util.CollectionUtils;

import reactor.core.publisher.Mono;

/**
 * {@code Californium} specific {@link CoapResource} creating an integration
 * with {@code CoapHandler} to fulfil contract with {@link ServerCoapExchange}.
 *
 * @author Janne Valkealahti
 *
 */
public class CaliforniumCoapHandlerResource extends AbstractCoapResource {

	private static final Logger log = LoggerFactory.getLogger(CaliforniumCoapHandlerResource.class);
	private List<CoapMethod> allowedMethods = null;
	private final CoapHandler coapHanlder;

	/**
	 * Instantiates a new californium coap handler resource.
	 *
	 * @param name the name
	 * @param coapHandler the coap handler
	 */
	public CaliforniumCoapHandlerResource(String name, CoapHandler coapHandler) {
		super(name);
		this.coapHanlder = coapHandler;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		if (isMethodAllowed(exchange)) {
			handleRequest(exchange);
		} else {
			super.handleGET(exchange);
		}
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		if (isMethodAllowed(exchange)) {
			handleRequest(exchange);
		} else {
			super.handlePOST(exchange);
		}
	}

	@Override
	public void handlePUT(CoapExchange exchange) {
		if (isMethodAllowed(exchange)) {
			handleRequest(exchange);
		} else {
			super.handlePUT(exchange);
		}
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		if (isMethodAllowed(exchange)) {
			handleRequest(exchange);
		} else {
			super.handleDELETE(exchange);
		}
	}

	public void setAllowedMethods(List<CoapMethod> allowedMethods) {
		this.allowedMethods = allowedMethods;
	}

	private void handleRequest(CoapExchange exchange) {
		log.trace("Handling exchange {}", exchange);
		CoapHeaders coapHeaders = new CoapHeaders();
		List<Option> others = exchange.getRequestOptions().getOthers();
		for (Option option : others) {
			coapHeaders.add(option.getNumber(), option.getStringValue().getBytes());
		}

		GenericServerCoapRequest request = new GenericServerCoapRequest(exchange.getRequestPayload(), coapHeaders);
		request.setMethod(CoapMethod.resolve(exchange.getRequestCode().name()));
		request.setContentFormat(exchange.getRequestOptions().getContentFormat());
		request.setUriPath(exchange.getRequestOptions().getUriPathString());

		GenericServerCoapResponse serverCoapResponse = new GenericServerCoapResponse();
		ServerCoapExchange serverCoapExchange = new DefaultServerCoapExchange(request, serverCoapResponse);
		Mono<Void> handle = coapHanlder.handle(serverCoapExchange);
		handle
			.onErrorResume(ex -> {
					if (log.isTraceEnabled()) {
						log.trace("Error in handler", ex);
					}
					exchange.respond(ResponseCode.BAD_REQUEST);
					return Mono.empty();
				})
			.doOnSuccess(c -> {
					if (log.isTraceEnabled()) {
						log.trace("About to send response");
					}
					ServerCoapResponse response = serverCoapExchange.getResponse();
					ResponseCode responseCode = response.getStatus() != null
							? ResponseCode.valueOf(response.getStatus().value) : ResponseCode.CREATED;
					exchange.respond(responseCode, response.getBody());
					if (log.isTraceEnabled()) {
						log.trace("Sent response {}", response);
					}
				})
			.subscribe();
	}

	private boolean isMethodAllowed(CoapExchange exchange) {
		if (allowedMethods == null) {
			return true;
		} else {
			return CollectionUtils.containsInstance(allowedMethods, CoapMethod.resolve(exchange.getRequestCode().name()));
		}
	}
}

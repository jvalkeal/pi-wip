package org.springframework.cloud.iot.coap.californium;

import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.springframework.cloud.iot.coap.CoapHeaders;
import org.springframework.cloud.iot.coap.CoapMethod;
import org.springframework.cloud.iot.coap.server.CoapHandler;
import org.springframework.cloud.iot.coap.server.DefaultServerCoapExchange;
import org.springframework.cloud.iot.coap.server.ServerCoapExchange;
import org.springframework.cloud.iot.coap.server.ServerCoapResponse;
import org.springframework.cloud.iot.coap.support.GenericServerCoapRequest;
import org.springframework.util.CollectionUtils;

import reactor.core.publisher.Mono;

public class CaliforniumCoapHandlerResource  extends AbstractCoapResource {

	private List<CoapMethod> allowedMethods = null;
	private final CoapHandler handler;

	public CaliforniumCoapHandlerResource(String name, CoapHandler handler) {
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

		ServerCoapExchange serverCoapExchange = new DefaultServerCoapExchange();
		Mono<Void> handle = handler.handle(serverCoapExchange);
		handle.doOnSuccess(c -> {
			ServerCoapResponse response = serverCoapExchange.getResponse();
			ResponseCode responseCode = response.getStatus() != null ? ResponseCode.valueOf(response.getStatus().value) : ResponseCode.CREATED;
			exchange.respond(responseCode, response.getBody());
		}).subscribe();

	}

	private boolean isMethodAllowed(CoapExchange exchange) {
		if (allowedMethods == null) {
			return true;
		} else {
			return CollectionUtils.containsInstance(allowedMethods, CoapMethod.resolve(exchange.getRequestCode().name()));
		}
	}
}

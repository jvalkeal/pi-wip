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
package org.springframework.coap.californium;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.coap.CoapHeaders;
import org.springframework.coap.CoapMethod;
import org.springframework.coap.server.CoapHandler;
import org.springframework.coap.server.ServerCoapExchange;
import org.springframework.coap.server.ServerCoapObservableContext;
import org.springframework.coap.server.ServerCoapResponse;
import org.springframework.coap.server.support.DefaultServerCoapExchange;
import org.springframework.coap.server.support.DefaultServerCoapObservableContext;
import org.springframework.coap.server.support.GenericServerCoapRequest;
import org.springframework.coap.server.support.GenericServerCoapResponse;
import org.springframework.coap.server.support.RequestPath;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@code Californium} specific {@link CoapResource} creating an integration
 * with {@code CoapHandler} to fulfil contract with {@link ServerCoapExchange}.
 *
 * @author Janne Valkealahti
 *
 */
public class CaliforniumCoapHandlerResource extends AbstractCaliforniumCoapResource {

	private static final Logger log = LoggerFactory.getLogger(CaliforniumCoapHandlerResource.class);
	private List<CoapMethod> allowedMethods = null;
	private final CoapHandler coapHandler;
	private HashMap<Exchange, ServerCoapObservableContext> contexts = new HashMap<>();

	/**
	 * Instantiates a new californium coap handler resource.
	 *
	 * @param name the name
	 * @param coapHandler the coap handler
	 */
	public CaliforniumCoapHandlerResource(String name, CoapHandler coapHandler) {
		super(name);

		setObservable(true);
		setObserveType(Type.CON);

		this.coapHandler = coapHandler;

		addObserver(new CaliforniumResourceObserverAdapter() {
			@Override
			public void addedObserveRelation(ObserveRelation relation) {
				log.info("addedObserveRelation {} {}", relation, relation.getExchange());
				ServerCoapObservableContext context = contexts.get(relation.getExchange());
				if (context != null && context.getObservableSource() != null) {
					Flux<Object> observableSource = context.getObservableSource();
					if (observableSource != null) {
						Disposable disposable = observableSource.doOnNext(c -> {
							context.setResult(c);
							changed(r -> {
								return ObjectUtils.nullSafeEquals(relation, r);
							});
						}).subscribe();
						context.setDisposable(disposable);
					}
				}
			}

			@Override
			public void removedObserveRelation(ObserveRelation relation) {
				log.info("removedObserveRelation {}", relation);
				ServerCoapObservableContext context = contexts.get(relation.getExchange());
				if (context != null && context.getDisposable() != null) {
					context.getDisposable().dispose();
				}
				contexts.remove(context);
			}
		});
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
		log.trace("Handling exchange {} {} {}", exchange, exchange.getRequestOptions(), exchange.advanced());
		ServerCoapObservableContext context = contexts.get(exchange.advanced());
		if (exchange.getRequestOptions().hasObserve()) {
			if (context == null) {
				context = new DefaultServerCoapObservableContext(null);
				contexts.put(exchange.advanced(), context);
			}
		}
		ServerCoapExchange serverCoapExchange = createServerCoapExchange(exchange, context);

		Mono<Void> handle = coapHandler.handle(serverCoapExchange);
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

	private ServerCoapExchange createServerCoapExchange(CoapExchange exchange, ServerCoapObservableContext context) {
		CoapHeaders coapHeaders = new CoapHeaders();
		List<Option> others = exchange.getRequestOptions().getOthers();
		for (Option option : others) {
			coapHeaders.add(option.getNumber(), option.getStringValue().getBytes());
		}

		GenericServerCoapRequest request = new GenericServerCoapRequest(exchange.getRequestPayload(), coapHeaders);
		request.setMethod(CoapMethod.resolve(exchange.getRequestCode().name()));
		request.setContentFormat(exchange.getRequestOptions().getContentFormat());
		URI uri;
		try {
			uri = new URI(exchange.advanced().getRequest().getURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Invalid URI path: \"" + exchange.getRequestOptions().getUriPathString() + "\"");
		}
		request.setPath(RequestPath.parse(uri, ""));

		GenericServerCoapResponse serverCoapResponse = new GenericServerCoapResponse();
		return new DefaultServerCoapExchange(request, serverCoapResponse, context);
	}

	private boolean isMethodAllowed(CoapExchange exchange) {
		if (allowedMethods == null) {
			return true;
		} else {
			return CollectionUtils.containsInstance(allowedMethods, CoapMethod.resolve(exchange.getRequestCode().name()));
		}
	}
}

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
package org.springframework.cloud.iot.integration.coap.dsl;

import java.net.URI;
import java.util.function.Function;

import org.springframework.integration.expression.FunctionExpression;
import org.springframework.messaging.Message;

/**
 * The Spring Integration Coap components Factory.
 *
 * @author Janne Valkealahti
 *
 */
public final class Coap {

	/**
	 * Create an {@link CoapInboundGatewaySpec} builder for request-reply gateway.
	 *
	 * @return the CoapInboundGatewaySpec instance
	 */
	public static CoapInboundGatewaySpec inboundGateway() {
		return new CoapInboundGatewaySpec(true);
	}

	/**
	 * Create an {@link CoapInboundGatewaySpec} builder for one-way adapter.
	 *
	 * @return the CoapInboundGatewaySpec instance
	 */
	public static CoapInboundGatewaySpec inboundChannelAdapter() {
		return new CoapInboundGatewaySpec(false);
	}

	/**
	 * Create an {@link CoapOutboundGatewaySpec} builder for request-reply gateway.
	 *
	 * @param url the url
	 * @return the CoapOutboundGatewaySpec instance
	 */
	public static CoapOutboundGatewaySpec outboundGateway(URI url) {
		return new CoapOutboundGatewaySpec(url);
	}

	/**
	 * Create an {@link CoapOutboundGatewaySpec} builder for request-reply gateway.
	 *
	 * @param uri the uri
	 * @return the CoapOutboundGatewaySpec instance
	 */
	public static CoapOutboundGatewaySpec outboundGateway(String uri) {
		return new CoapOutboundGatewaySpec(uri);
	}

	/**
	 * Create an {@link CoapOutboundGatewaySpec} builder for request-reply gateway.
	 *
	 * @param <P> the type of payload
	 * @param uriFunction the uri function
	 * @return the CoapOutboundGatewaySpec instance
	 */
	public static <P> CoapOutboundGatewaySpec outboundGateway(Function<Message<P>, ?> uriFunction) {
		return new CoapOutboundGatewaySpec(new FunctionExpression<>(uriFunction));
	}

	private Coap() {
		super();
	}
}

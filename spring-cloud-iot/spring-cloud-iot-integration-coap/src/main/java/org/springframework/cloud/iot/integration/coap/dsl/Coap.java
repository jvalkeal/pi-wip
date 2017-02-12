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

import org.springframework.cloud.iot.integration.coap.inbound.CoapInboundGateway;

/**
 * The Spring Integration Coap components Factory.
 *
 * @author Janne Valkealahti
 *
 */
public final class Coap {

	public static CoapOutboundGatewaySpec outboundGateway(URI url) {
		return new CoapOutboundGatewaySpec(url);
	}

	public static CoapInboundGatewaySpec inboundGateway() {
		return new CoapInboundGatewaySpec(new CoapInboundGateway());
	}

	public static CoapInboundGatewaySpec inboundChannelAdapter() {
		return new CoapInboundGatewaySpec(new CoapInboundGateway(false));
	}

	private Coap() {
		super();
	}
}

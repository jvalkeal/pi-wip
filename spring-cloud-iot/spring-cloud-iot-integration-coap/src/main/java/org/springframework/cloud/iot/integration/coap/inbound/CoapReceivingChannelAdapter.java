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
package org.springframework.cloud.iot.integration.coap.inbound;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.springframework.integration.endpoint.MessageProducerSupport;

/**
 *
 * @author Janne Valkealahti
 *
 */
public class CoapReceivingChannelAdapter extends MessageProducerSupport {

	private final int port;

	private volatile IntegrationCoapServer server;

	/**
	 * Instantiates a new coap receiving channel adapter.
	 *
	 * @param port the port
	 */
	public CoapReceivingChannelAdapter(int port) {
		this.port = port;
	}

	@Override
	protected void doStart() {
		this.server = new IntegrationCoapServer();
		this.server.start();
	}

	@Override
	protected void doStop() {
		this.server.stop();
	}

	private class IntegrationCoapServer extends CoapServer {

		public IntegrationCoapServer() {
			super(port);
			add(new Resource());
		}

	}

	private class Resource extends CoapResource {

		public Resource() {
			super("spring-integration-coap");
			getAttributes().setTitle("Integration Source Resource");
		}

		@Override
		public void handleGET(CoapExchange exchange) {
			sendMessage(getMessageBuilderFactory()
					.withPayload("hello")
					.build());
			exchange.respond(ResponseCode.CREATED, "hello");
		}

		@Override
		public void handlePOST(CoapExchange exchange) {
			sendMessage(getMessageBuilderFactory()
					.withPayload(new String(exchange.getRequestPayload()))
					.setHeader("coap_method", exchange.getRequestCode().name())
					.setHeader("coap_remoteIp", exchange.getSourceAddress().getHostAddress())
					.setHeader("coap_remotePort", exchange.getSourcePort())
					.build());

			exchange.respond(ResponseCode.CREATED);
		}

	}

}

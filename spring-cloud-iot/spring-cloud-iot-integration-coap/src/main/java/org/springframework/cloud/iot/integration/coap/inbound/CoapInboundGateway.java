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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.springframework.cloud.iot.integration.coap.server.AbstractCoapResource;
import org.springframework.cloud.iot.integration.coap.server.CoapServerFactoryBean;
import org.springframework.integration.gateway.MessagingGatewaySupport;
import org.springframework.messaging.Message;

/**
 * Inbound gateway using Californium {@link CoapServer}.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapInboundGateway extends MessagingGatewaySupport {

	private CoapServerFactoryBean factory;

	@Override
	protected void onInit() throws Exception {
		super.onInit();
		factory = new CoapServerFactoryBean();
		List<Resource> coapResources = new ArrayList<>();
		coapResources.add(new InboundHandlingResource());
		factory.setCoapResources(coapResources);
		factory.afterPropertiesSet();
		factory.getObject();
	}

	@Override
	protected void doStart() {
	}

	@Override
	protected void doStop() {
		if (factory != null) {
			try {
				factory.destroy();
			} catch (Exception e) {
				logger.info("Error stopping server", e);
			}
		}
	}

	@Override
	public String getComponentType() {
		return "coap:inbound-gateway";
	}

	private Message<?> doOnMessage(Message<?> message) {
		Message<?> reply = this.sendAndReceiveMessage(message);
		return reply;
	}

	private class InboundHandlingResource extends AbstractCoapResource {

		public InboundHandlingResource() {
			super("spring-integration-coap");
			getAttributes().setTitle("Integration Source Resource");
		}

		@Override
		public void handleGET(CoapExchange exchange) {
			Message<?> reply = doOnMessage(getMessageBuilderFactory().withPayload("hello").build());
			exchange.respond(ResponseCode.CREATED, reply.getPayload().toString());
		}

		@Override
		public void handlePOST(CoapExchange exchange) {
			logger.info("XXX5 " + exchange.getRequestOptions());
			logger.info("XXX5 " + exchange.getRequestOptions().getLocationPath());
			logger.info("XXX5 " + exchange.getRequestOptions().getUriPath());
			Message<?> reply = doOnMessage(getMessageBuilderFactory().withPayload("hello").build());
			exchange.respond(ResponseCode.CREATED, reply.getPayload().toString());
		}

	}

}

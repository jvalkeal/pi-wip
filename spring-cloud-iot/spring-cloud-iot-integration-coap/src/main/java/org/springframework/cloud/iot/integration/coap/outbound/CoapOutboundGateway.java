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
package org.springframework.cloud.iot.integration.coap.outbound;

import org.springframework.cloud.iot.integration.coap.client.CoapOperations;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;

/**
 * Inbound gateway using {@link CoapOperations}.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapOutboundGateway extends AbstractReplyProducingMessageHandler {

	private final CoapOperations coapOperations;

	public CoapOutboundGateway(CoapOperations coapOperations) {
		this.coapOperations = coapOperations;
	}

	@Override
	protected Object handleRequestMessage(Message<?> requestMessage) {
		return coapOperations.getForObject(String.class);
	}

}

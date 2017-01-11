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

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;

public class CoapRequestExecutingMessageHandler extends AbstractReplyProducingMessageHandler  {

	@Override
	protected Object handleRequestMessage(Message<?> requestMessage) {
		try {
			URI uri = new URI("coap", null, "localhost", 15683, "/spring-integration-coap", null, null);
			CoapClient client = new CoapClient(uri);

			CoapResponse response = client.post("bar".getBytes(), 0);
			return response;
		} catch (URISyntaxException e) {
			throw new RuntimeException();
		}
	}

}

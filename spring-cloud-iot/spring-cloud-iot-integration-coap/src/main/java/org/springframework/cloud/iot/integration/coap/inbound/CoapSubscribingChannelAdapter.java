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

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.support.MessageBuilder;

public class CoapSubscribingChannelAdapter extends MessageProducerSupport {


	@Override
	protected void doStart() {
		super.doStart();

		try {
			URI uri = new URI("coap", null, "localhost", 15683, "/spring-integration-coap", null, null);
			CoapClient client = new CoapClient(uri);
			client.observe(new CoapHandler() {

				@Override
				public void onLoad(CoapResponse response) {
					sendMessage(MessageBuilder.withPayload(response).build());
				}

				@Override
				public void onError() {
					sendMessage(MessageBuilder.withPayload("error").build());
				}
			});
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

}

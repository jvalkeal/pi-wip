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

import static org.junit.Assert.assertNotNull;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.junit.Test;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;

public class CoapSubscribingChannelAdapterTests {

	@Test
	public void test() {
		IntegrationCoapServer server = new IntegrationCoapServer();
		server.start();

		CoapSubscribingChannelAdapter adapter = new CoapSubscribingChannelAdapter();
		QueueChannel outputChannel = new QueueChannel();
		adapter.setOutputChannel(outputChannel);
		adapter.start();

		Message<?> request = outputChannel.receive(10000);
		assertNotNull(request);

		server.resource.changed();

		request = outputChannel.receive(10000);
		assertNotNull(request);
		server.stop();
	}

	private class IntegrationCoapServer extends CoapServer {

		Resource resource;

		public IntegrationCoapServer() {
			super(15683);
			resource = new Resource();
			add(resource);
		}

	}

	private class Resource extends CoapResource {

		public Resource() {
			super("spring-integration-coap");
			getAttributes().setTitle("Integration Source Resource");
		}

		@Override
		public void handleGET(CoapExchange exchange) {
			exchange.setMaxAge(1); // the Max-Age value should match the update interval
			exchange.respond("update");
		}

		@Override
		public void handleDELETE(CoapExchange exchange) {
			delete(); // will also call clearAndNotifyObserveRelations(ResponseCode.NOT_FOUND)
			exchange.respond("DELETED");
		}

		@Override
		public void handlePUT(CoapExchange exchange) {
			// ...
			exchange.respond("CHANGED");
			changed(); // notify all observers
		}

	}

}

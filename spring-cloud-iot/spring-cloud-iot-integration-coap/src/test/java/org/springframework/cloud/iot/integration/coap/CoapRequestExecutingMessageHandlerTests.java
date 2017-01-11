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
package org.springframework.cloud.iot.integration.coap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.junit.Test;
import org.springframework.cloud.iot.integration.coap.inbound.CoapReceivingChannelAdapter;
import org.springframework.cloud.iot.integration.coap.outbound.CoapRequestExecutingMessageHandler;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

public class CoapRequestExecutingMessageHandlerTests {

	@Test
	public void test() {
		CoapReceivingChannelAdapter adapter = new CoapReceivingChannelAdapter(15683);
		QueueChannel outputChannel = new QueueChannel();
		adapter.setOutputChannel(outputChannel);
		adapter.start();

		CoapRequestExecutingMessageHandler handler = new CoapRequestExecutingMessageHandler();
		QueueChannel replyChannel = new QueueChannel();
		handler.setOutputChannel(replyChannel);
		handler.handleMessage(MessageBuilder.withPayload("hello").build());
		Message<?> receive = outputChannel.receive();
		assertNotNull(receive);
		Message<?> receive2 = replyChannel.receive();
		assertNotNull(receive2);
		adapter.stop();
	}

}

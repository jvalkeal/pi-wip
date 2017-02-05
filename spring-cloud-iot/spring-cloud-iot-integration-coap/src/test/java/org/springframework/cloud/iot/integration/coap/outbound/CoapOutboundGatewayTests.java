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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import org.springframework.cloud.iot.integration.coap.AbstractCoapTests;
import org.springframework.cloud.iot.integration.coap.TestCoapServerConfiguration;
import org.springframework.cloud.iot.integration.coap.client.CoapTemplate;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

public class CoapOutboundGatewayTests extends AbstractCoapTests {

	@Test
	public void test() throws URISyntaxException {
		context.register(TestCoapServerConfiguration.class);
		context.refresh();

		URI uri = new URI("coap", null, "localhost", 5683, "/testresource1", null, null);

		CoapOutboundGateway gateway = new CoapOutboundGateway(uri);
		QueueChannel replyChannel = new QueueChannel();
		gateway.setOutputChannel(replyChannel);
		gateway.setExpectedResponseType(String.class);

		gateway.handleMessage(MessageBuilder.withPayload("dummy").build());

		Message<?> receive = replyChannel.receive();
		assertThat(receive, notNullValue());
		assertThat(receive.getPayload(), is("hello"));
	}

	@Test
	public void test2() throws URISyntaxException {
		context.register(TestCoapServerConfiguration.class);
		context.refresh();

		URI uri = new URI("coap", null, "localhost", 5683, "/testresource4", null, null);

		CoapOutboundGateway gateway = new CoapOutboundGateway(uri);
		QueueChannel replyChannel = new QueueChannel();
		gateway.setOutputChannel(replyChannel);
		gateway.setExpectedResponseType(String.class);

		gateway.handleMessage(MessageBuilder.withPayload("dummy").build());

		Message<?> receive = replyChannel.receive();
		assertThat(receive, notNullValue());
		assertThat(receive.getPayload(), is("echo:dummy"));
	}

//	@Test
	public void test3() throws URISyntaxException {
		context.register(TestCoapServerConfiguration.class);
		context.refresh();

		URI uri = new URI("coap", null, "localhost", 5683, "/testresource1", null, null);

		CoapOutboundGateway gateway = new CoapOutboundGateway(uri);
		QueueChannel replyChannel = new QueueChannel();
		gateway.setOutputChannel(replyChannel);

		gateway.handleMessage(MessageBuilder.withPayload("dummy").build());
	}

	@Override
	protected AnnotationConfigApplicationContext buildContext() {
		return new AnnotationConfigApplicationContext();
	}
}

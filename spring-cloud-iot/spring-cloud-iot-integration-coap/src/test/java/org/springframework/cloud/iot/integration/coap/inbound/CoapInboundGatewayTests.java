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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.net.URI;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.junit.Test;
import org.springframework.cloud.iot.coap.californium.CaliforniumCoapServerFactory;
import org.springframework.cloud.iot.coap.californium.CoapTemplate;
import org.springframework.cloud.iot.coap.server.CoapServer;
import org.springframework.cloud.iot.integration.coap.AbstractCoapTests;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.handler.ServiceActivatingHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

public class CoapInboundGatewayTests extends AbstractCoapTests {

	@Override
	protected AnnotationConfigApplicationContext buildContext() {
		return new AnnotationConfigApplicationContext();
	}

	@Test
	public void testSimpleExpectReply() throws Exception {
		context.register(TestConfig1.class);
		context.refresh();
		CoapInboundGateway cig = context.getBean(CoapInboundGateway.class);
		int port = cig.getListeningCoapServerPort();

		DirectChannel requestChannel = context.getBean("requestChannel", DirectChannel.class);
		final ServiceActivatingHandler handler = new ServiceActivatingHandler(new Service());
		requestChannel.subscribe(new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				handler.handleMessage(message);
			}
		});

		URI uri = new URI("coap", null, "localhost", port, "/spring-integration-coap", null, null);
		CoapTemplate template = new CoapTemplate();
		String object = template.postForObject(uri, "hello", String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("Echo:hello"));
	}

	@Test
	public void testSimpleExpectReplyWithPassedServer() throws Exception {
		context.register(TestConfig4.class);
		context.refresh();
		CoapInboundGateway cig = context.getBean(CoapInboundGateway.class);
		int port = cig.getListeningCoapServerPort();

		DirectChannel requestChannel = context.getBean("requestChannel", DirectChannel.class);
		final ServiceActivatingHandler handler = new ServiceActivatingHandler(new Service());
		requestChannel.subscribe(new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				handler.handleMessage(message);
			}
		});

		URI uri = new URI("coap", null, "localhost", port, "/spring-integration-coap", null, null);
		CoapTemplate template = new CoapTemplate();
		String object = template.postForObject(uri, "hello", String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("Echo:hello"));
	}

	@Test
	public void testSimpleExpectNoReply() throws Exception {
		context.register(TestConfig2.class);
		context.refresh();
		CoapInboundGateway cig = context.getBean(CoapInboundGateway.class);
		int port = cig.getListeningCoapServerPort();

		QueueChannel requestChannel = context.getBean("requestChannel", QueueChannel.class);


		URI uri = new URI("coap", null, "localhost", port, "/spring-integration-coap", null, null);
		CoapTemplate template = new CoapTemplate();
		String object = template.postForObject(uri, "hello", String.class);
		assertThat(object, nullValue());
		Message<?> receive = requestChannel.receive(0);
		assertThat(receive.getPayload().toString(), is("hello"));
	}

	@Test
	public void testSimpleSendWithoutContentFormat() throws Exception {
		context.register(TestConfig3.class);
		context.refresh();
		CoapInboundGateway cig = context.getBean(CoapInboundGateway.class);
		int port = cig.getListeningCoapServerPort();

		DirectChannel requestChannel = context.getBean("requestChannel", DirectChannel.class);
		final ServiceActivatingHandler handler = new ServiceActivatingHandler(new Service());
		requestChannel.subscribe(new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				handler.handleMessage(message);
			}
		});

		// need to use californium client as CoapTemplate always sets content-format
		URI uri = new URI("coap", null, "localhost", port, "/spring-integration-coap", null, null);
		CoapClient client = new CoapClient(uri);
		CoapResponse response = client.post("hello", -1);
		assertThat(response.isSuccess(), is(false));
		assertThat(response.getCode(), is(ResponseCode.INTERNAL_SERVER_ERROR));
		// TODO: change this when we're able to define default behaviour with missing content-format
//		String object = new String(response.getPayload());
//		assertThat(object, notNullValue());
//		assertThat(object, is("Echo:hello"));
	}

	@Configuration
	public static class TestConfig1 {

		@Bean
		public DirectChannel requestChannel() {
			return new DirectChannel();
		}

		@Bean
		public CoapInboundGateway coapInboundGateway() {
			CoapInboundGateway gateway = new CoapInboundGateway(true);
			gateway.setCoapServerPort(0);
			gateway.setRequestChannel(requestChannel());
			return gateway;
		}
	}

	@Configuration
	public static class TestConfig2 {

		@Bean
		public QueueChannel requestChannel() {
			return new QueueChannel();
		}

		@Bean
		public CoapInboundGateway coapInboundGateway() {
			CoapInboundGateway gateway = new CoapInboundGateway(false);
			gateway.setCoapServerPort(0);
			gateway.setRequestChannel(requestChannel());
			return gateway;
		}
	}

	@Configuration
	public static class TestConfig3 {

		@Bean
		public DirectChannel requestChannel() {
			return new DirectChannel();
		}

		@Bean
		public CoapInboundGateway coapInboundGateway() {
			CoapInboundGateway gateway = new CoapInboundGateway(true);
			gateway.setCoapServerPort(0);
			gateway.setRequestChannel(requestChannel());
			return gateway;
		}
	}

	@Configuration
	public static class TestConfig4 {

		@Bean
		public DirectChannel requestChannel() {
			return new DirectChannel();
		}

		@Bean
		public CoapInboundGateway coapInboundGateway() {
			CoapInboundGateway gateway = new CoapInboundGateway(coapServer(), true);
			gateway.setCoapServerPort(0);
			gateway.setRequestChannel(requestChannel());
			return gateway;
		}

		@Bean
		public CoapServer coapServer() {
			CaliforniumCoapServerFactory factory = new CaliforniumCoapServerFactory();
			return factory.getCoapServer();
		}
	}

	private class Service {

		@SuppressWarnings("unused")
		public String serviceMethod(String message) {
			return "Echo:" + message;
		}
	}
}

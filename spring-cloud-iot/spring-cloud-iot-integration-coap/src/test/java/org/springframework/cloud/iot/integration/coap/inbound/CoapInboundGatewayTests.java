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
import static org.junit.Assert.assertThat;

import java.net.URI;

import org.junit.Test;
import org.springframework.cloud.iot.integration.coap.AbstractCoapTests;
import org.springframework.cloud.iot.integration.coap.client.CoapTemplate;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
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
	public void testSimpleInbound() throws Exception {
		context.register(TestConfig1.class);
		context.refresh();

		DirectChannel requestChannel = context.getBean("requestChannel", DirectChannel.class);
		final ServiceActivatingHandler handler = new ServiceActivatingHandler(new Service());
		requestChannel.subscribe(new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				handler.handleMessage(message);
			}
		});

		URI uri = new URI("coap", null, "localhost", 5683, "/spring-integration-coap/1/2/3", null, null);
		CoapTemplate template = new CoapTemplate(uri);
		String object = template.postForObject("hello", String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("Echo:hello"));
	}

	@Configuration
	public static class TestConfig1 {

		@Bean
		public DirectChannel requestChannel() {
			return new DirectChannel();
		}

		@Bean
		public CoapInboundGateway coapInboundGateway() {
			CoapInboundGateway coapInboundGateway = new CoapInboundGateway();
			coapInboundGateway.setRequestChannel(requestChannel());
			return coapInboundGateway;
		}
	}

	private class Service {

		@SuppressWarnings("unused")
		public String serviceMethod(String message) {
			return "Echo:" + message;
		}
	}
}

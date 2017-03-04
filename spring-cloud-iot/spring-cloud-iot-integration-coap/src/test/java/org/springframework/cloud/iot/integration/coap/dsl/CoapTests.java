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
package org.springframework.cloud.iot.integration.coap.dsl;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.iot.coap.client.CoapOperations;
import org.springframework.cloud.iot.coap.client.CoapTemplate;
import org.springframework.cloud.iot.integration.coap.inbound.CoapInboundGateway;
import org.springframework.cloud.iot.integration.coap.outbound.CoapOutboundGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DirtiesContext
public class CoapTests {

	@Autowired
	private CoapInboundGateway coapInboundGateway;

	@Autowired
	private CoapOutboundGateway coapOutboundGateway;

	@Autowired
	private DirectChannel requestChannel;

	@Test
	public void testCoapInboundGatewayFlow() throws Exception {
		assertThat(coapInboundGateway, notNullValue());
	}

	@Test
	public void testCoapOutboundGatewayFlow() throws Exception {
		assertThat(coapOutboundGateway, notNullValue());
		coapOutboundGateway.setExpectedResponseType(String.class);
		requestChannel.send(MessageBuilder.withPayload("hello").build());
	}

	@Configuration
	@EnableIntegration
	public static class ContextConfiguration {

		@Bean
		public CoapOperations coapOperations() throws URISyntaxException {
			return new CoapTemplate();
		}

		@Bean
		public DirectChannel requestChannel() {
			return MessageChannels.direct().get();
		}

		@Bean
		public IntegrationFlow coapInboundFlow() {
			return IntegrationFlows
					.from(Coap.inboundGateway())
					.handle((p, h) -> "echo:" + p)
					.get();
		}

		@Bean
		public IntegrationFlow coapOutboundFlow() throws URISyntaxException {
			return IntegrationFlows
				.from(requestChannel())
				.handle(Coap.outboundGateway(new URI("coap", null, "localhost", 5683, "/spring-integration-coap", null, null)))
				.handle(p -> {System.out.println("XXXX " + p);})
				.get();
		}
	}

}

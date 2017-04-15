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
package org.springframework.cloud.iot.gateway.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.iot.gateway.service.rest.RestGatewayServiceResponse;
import org.springframework.cloud.iot.integration.coap.dsl.Coap;
import org.springframework.cloud.iot.integration.xbee.dsl.XBee;
import org.springframework.cloud.iot.xbee.XBeeReceiver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.router.AbstractMappingMessageRouter;
import org.springframework.messaging.Message;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for IoT gateway server.
 *
 * @author Janne Valkealahti
 */
@Configuration
public class IotGatewayServerConfiguration {

	@Configuration
	@ConditionalOnProperty(prefix = "spring.cloud.iot.gateway.rest", name = "enabled", havingValue = "true", matchIfMissing = false)
	public static class IotRestGatewayServerConfiguration {

		@Bean
		public IntegrationFlow iotGatewayServerCoapXxx() {
			return IntegrationFlows
					.from("RestGatewayService")
//					.transform(new JsonToObjectTransformer())
					.handle((p, h) -> {
						System.out.println("XXX6 " + p);
						System.out.println("XXX6 " + p.getClass());
						RestTemplate t = new RestTemplate();
						String xxx = t.getForObject("http://example.com", String.class);
						System.out.println("XXX6 " + xxx);
						return new RestGatewayServiceResponse(xxx);
					})
					.transform(new ObjectToJsonTransformer())
					.get();
		}

		@Bean
		public IntegrationFlow iotGatewayServerCoapInboundFlow() {
			return IntegrationFlows
					.from(Coap.inboundGateway())
					.route(new ServiceRouter())
					.get();
		}
	}

	public static class ServiceRouter extends AbstractMappingMessageRouter {

		@Override
		protected List<Object> getChannelKeys(Message<?> message) {
			System.out.println("XXX2 " + message);
			List<Object> channels = new ArrayList<>();
			Object value = message.getHeaders().get("9999");
			if (value instanceof List) {
				for (Object o : ((List)value).toArray()) {
					if (o instanceof byte[]) {
						System.out.println("XXX3 " + new String(((byte[])o)));
						channels.add(new String(((byte[])o)));
					}
				}
			}
			return channels;
		}
	}

	@Configuration
	@ConditionalOnProperty(prefix = "spring.cloud.iot.xbee", name = "enabled", havingValue = "true", matchIfMissing = false)
	public static class XBeeServerConfiguration {

		@Bean
		public IntegrationFlow iotGatewayServerXBeeInboundFlow(XBeeReceiver xbeeReceiver) {
			return IntegrationFlows
					.from(XBee.inboundGateway(xbeeReceiver))
					.<byte[]>log(f -> new String(f.getPayload()))
					.get();
		}
	}
}

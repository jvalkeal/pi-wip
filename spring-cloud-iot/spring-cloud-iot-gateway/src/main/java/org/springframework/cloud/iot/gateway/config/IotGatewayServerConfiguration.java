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
import org.springframework.cloud.iot.gateway.server.GatewayServer;
import org.springframework.cloud.iot.gateway.service.rest.RestGatewayServiceConfiguration;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.router.AbstractMappingMessageRouter;
import org.springframework.integration.router.MessageRouter;
import org.springframework.messaging.Message;
import org.springframework.util.StringUtils;

/**
 * Configuration for IoT gateway server.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
@EnableBinding(GatewayServer.class)
@Import(RestGatewayServiceConfiguration.class)
public class IotGatewayServerConfiguration {

	@Configuration
	@ConditionalOnProperty(prefix = "spring.cloud.iot.gateway.rest", name = "enabled", havingValue = "true", matchIfMissing = false)
	public static class IotRestGatewayServerConfiguration {

		@Bean
		public IntegrationFlow iotGatewayServerCoapToRouterFlow() {
			return IntegrationFlows
					.from(GatewayServer.INPUT)
					.route(iotGatewayServerServiceRouter())
					.get();
		}

		@Bean
		public ServiceRouter iotGatewayServerServiceRouter() {
			return new ServiceRouter();
		}
	}

	/**
	 * {@link MessageRouter} implementation which routes messages based on header.
	 */
	private static class ServiceRouter extends AbstractMappingMessageRouter {

		@Override
		protected List<Object> getChannelKeys(Message<?> message) {
			List<Object> channels = new ArrayList<>();
			Object value = message.getHeaders().get("iotGatewayServiceRoute");
			if (value instanceof List) {
				for (Object o : ((List)value).toArray()) {
					if (o instanceof byte[]) {
						channels.add(new String(((byte[])o)));
					}
				}
			} else if (value instanceof String) {
				channels.add(value);
			}
			logger.debug("ServiceRouter resolved to channels " + StringUtils.collectionToCommaDelimitedString(channels));
			return channels;
		}
	}
}

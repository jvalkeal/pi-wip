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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.iot.integration.coap.dsl.Coap;
import org.springframework.cloud.iot.integration.xbee.dsl.XBee;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

import com.digi.xbee.api.XBeeDevice;

/**
 * Configuration for IoT gateway server.
 *
 * @author Janne Valkealahti
 */
@Configuration
public class IotGatewayServerConfiguration {

	@Configuration
	@ConditionalOnProperty(prefix = "spring.cloud.iot.coap", name = "enabled", havingValue = "true", matchIfMissing = false)
	public static class CoapServerConfiguration {

		@Bean
		public IntegrationFlow iotGatewayServerCoapInboundFlow() {
			return IntegrationFlows
					.from(Coap.inboundGateway())
					.log()
					.get();
		}
	}

	@Configuration
	@ConditionalOnProperty(prefix = "spring.cloud.iot.xbee", name = "enabled", havingValue = "true", matchIfMissing = false)
	public static class XBeeServerConfiguration {

		@Bean
		public IntegrationFlow iotGatewayServerXBeeInboundFlow(XBeeDevice xbeeDevice) {
			return IntegrationFlows
					.from(XBee.inboundGateway(xbeeDevice))
					.log()
					.get();
		}

	}
}

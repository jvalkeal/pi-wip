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

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.iot.coap.californium.CoapTemplate;
import org.springframework.cloud.iot.coap.client.CoapOperations;
import org.springframework.cloud.iot.integration.coap.dsl.Coap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

/**
 * Configuration for IoT gateway client.
 *
 * @author Janne Valkealahti
 */
@Configuration
public class IotGatewayClientConfiguration {

	@Bean
	public CoapOperations iotCoapOperations() {
		// for convenience create template for user disposal
		return new CoapTemplate();
	}

	@Configuration
	@ConditionalOnProperty(prefix = "spring.cloud.iot.coap", name = "enabled", havingValue = "true", matchIfMissing = false)
	public static class CoapClientConfiguration {

		@Bean
		public IntegrationFlow iotGatewayServerCoapInboundFlow() throws URISyntaxException {
			return IntegrationFlows
					.from("iot.coapOutboundChannel")
					.handle(Coap.outboundGateway(new URI("")))
					.log()
					.get();
		}
	}

}

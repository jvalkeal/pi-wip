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
import org.springframework.cloud.iot.coap.californium.CoapTemplate;
import org.springframework.cloud.iot.coap.client.CoapOperations;
import org.springframework.cloud.iot.gateway.client.GatewayClient;
import org.springframework.cloud.iot.gateway.service.rest.RestGatewayService;
import org.springframework.cloud.iot.gateway.service.rest.RestGatewayServiceResponse;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.gateway.AnnotationGatewayProxyFactoryBean;

/**
 * Configuration for IoT gateway client.
 *
 * @author Janne Valkealahti
 */
@Configuration
@EnableBinding(GatewayClient.class)
public class IotGatewayClientConfiguration {

	@Bean
	public CoapOperations iotCoapOperations() {
		// for convenience create template for user disposal
		return new CoapTemplate();
	}

	@Bean
	public IntegrationFlow iotGatewayClientRequestFlow() {
		return IntegrationFlows
				.from(GatewayClient.OUTPUT_REQUEST)
				.channel(GatewayClient.OUTPUT)
				.get();
	}

	@Configuration
	@ConditionalOnProperty(prefix = "spring.cloud.iot.gateway.rest", name = "enabled", havingValue = "true", matchIfMissing = false)
	public static class IotRestGatewayClientConfiguration {

		@Bean
		public IntegrationFlow iotGatewayClientFlow() {
			return IntegrationFlows
					.from(GatewayClient.OUTPUT_REPLY)
					.<byte[], RestGatewayServiceResponse>transform(s -> {
							return new RestGatewayServiceResponse(new String(s));
						})
					.channel(GatewayClient.OUTPUT_RESPONSE)
					.get();
		}

		@Bean
		public AnnotationGatewayProxyFactoryBean restGatewayServiceGatewayProxyFactoryBean() {
			return new AnnotationGatewayProxyFactoryBean(RestGatewayService.class);
		}
	}
}

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
package org.springframework.cloud.iot.gateway.service.rest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(prefix = "spring.cloud.iot.gateway.rest", name = "enabled", havingValue = "true", matchIfMissing = false)
public class RestGatewayServiceConfiguration {

	@Bean
	public IntegrationFlow iotRestGatewayHandlerFlow() {
		return IntegrationFlows
				.from(RestGatewayService.ID)
				.transform(new JsonToObjectTransformer(RestGatewayServiceRequest.class))
				.handle(restGatewayServiceHandler(null))
				.transform(new ObjectToJsonTransformer())
				.get();
	}

	@ConditionalOnMissingBean(RestTemplate.class)
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public RestGatewayServiceHandler restGatewayServiceHandler(RestTemplate restTemplate) {
		return new RestGatewayServiceHandler(restTemplate);
	}
}

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
package org.springframework.cloud.iot.gateway.service.metric;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.iot.gateway.service.rest.RestGatewayServiceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.json.JsonToObjectTransformer;

@Configuration
@ConditionalOnProperty(prefix = "spring.cloud.iot.gateway.metric", name = "enabled", havingValue = "true", matchIfMissing = false)
public class MetricGatewayServiceConfiguration {

	@Bean
	public IntegrationFlow iotMetricGatewayHandlerFlow() {
		return IntegrationFlows
				.from(MetricGatewayService.ID)
				.transform(new JsonToObjectTransformer(MetricGatewayServiceRequest.class))
//				.handle(metricGatewayServiceHandler())
				.log("XXX")
				.get();
	}

//	@Bean
//	public RestGatewayServiceHandler metricGatewayServiceHandler() {
//		return null;
//	}
}

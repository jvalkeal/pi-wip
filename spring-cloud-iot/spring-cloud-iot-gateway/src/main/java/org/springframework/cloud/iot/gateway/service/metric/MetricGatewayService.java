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

import org.springframework.cloud.iot.gateway.IotGatewayConstants;
import org.springframework.cloud.iot.gateway.client.GatewayClient;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.GatewayHeader;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultHeaders = @GatewayHeader(name = IotGatewayConstants.HEADER_GATEWAY_SERVICE_ROUTE, value = MetricGatewayService.ID))
public interface MetricGatewayService {

	public final static String ID = "MetricGatewayService";

	@Gateway(requestChannel = GatewayClient.OUTPUT_REQUEST)
	void publish(MetricGatewayServiceRequest request);
}

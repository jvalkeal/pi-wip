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

import org.springframework.cloud.iot.gateway.IotGatewayConstants;
import org.springframework.cloud.iot.gateway.client.GatewayClient;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.GatewayHeader;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * Gateway service interface providing methods to execute restfull requests over
 * a spring cloud iot gateway server.
 *
 * @author Janne Valkealahti
 *
 */
@MessagingGateway(defaultHeaders = @GatewayHeader(name = IotGatewayConstants.HEADER_GATEWAY_SERVICE_ROUTE, value = RestGatewayService.ID))
public interface RestGatewayService {

	public final static String ID = "RestGatewayService";

	/**
	 * Execute a given {@link RestGatewayServiceRequest} and get a response in a
	 * form of a {@link RestGatewayServiceResponse}.
	 *
	 * @param request the rest gateway service request
	 * @return the rest gateways service response
	 */
	@Gateway(requestChannel = GatewayClient.OUTPUT_REQUEST,
			replyChannel = GatewayClient.OUTPUT_RESPONSE,
			headers = @GatewayHeader(name = "responseType", value = "org.springframework.cloud.iot.gateway.service.rest.RestGatewayServiceResponse"))
	RestGatewayServiceResponse execute(RestGatewayServiceRequest request);
}

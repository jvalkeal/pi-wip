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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.web.client.RestTemplate;

/**
 * {@code ServiceActivator} which processes a {@link RestGatewayServiceRequest}
 * reply {@link RestGatewayServiceResponse}.
 *
 * @author Janne Valkealahti
 *
 */
public class RestGatewayServiceHandler {

	private static final Logger log = LoggerFactory.getLogger(RestGatewayServiceHandler.class);
	private final RestTemplate restTemplate;

	/**
	 * Instantiates a new rest gateway service handler.
	 *
	 * @param restTemplate the rest template
	 */
	public RestGatewayServiceHandler(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@ServiceActivator
	public RestGatewayServiceResponse handle(RestGatewayServiceRequest request) {
		log.debug("Handling request {}", request);
		String body = restTemplate.getForObject(request.getUrl(), String.class);
		return new RestGatewayServiceResponse(body);
	}
}

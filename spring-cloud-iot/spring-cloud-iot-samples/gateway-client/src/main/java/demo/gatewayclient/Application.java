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
package demo.gatewayclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.iot.gateway.EnableIotGatewayClient;
import org.springframework.cloud.iot.gateway.service.rest.RestGatewayService;
import org.springframework.cloud.iot.gateway.service.rest.RestGatewayServiceRequest;
import org.springframework.scheduling.annotation.Scheduled;

@EnableIotGatewayClient
@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private RestGatewayService restGatewayService;

	@Scheduled(fixedDelay = 10000)
	public void doRestGatewayServiceCall() {
		log.info("Sending request for http://example.com");
		RestGatewayServiceRequest request = new RestGatewayServiceRequest("http://example.com");
		log.info("Result from RestGatewayService {}", restGatewayService.execute(request).getBody());
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

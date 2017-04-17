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
package org.springframework.cloud.iot.gateway;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.cloud.iot.gateway.service.rest.RestGatewayService;
import org.springframework.cloud.iot.gateway.service.rest.RestGatewayServiceRequest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;

public class GatewayTests extends AbstractGatewayTests {

	@Test
	public void testAutowire() {
		SpringApplication app = new SpringApplication(Config1.class, BeanConfig.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		ConfigurableApplicationContext context = app.run(new String[] { "--spring.cloud.iot.gateway.rest.enabled=true",
				"--spring.cloud.stream.bindings.iotGatewayClient.binder=coap" });
		context.close();
	}

	@Test
	public void testGateway() {
		SpringApplication app = new SpringApplication(Config1.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		ConfigurableApplicationContext context = app
				.run(new String[] { "--spring.cloud.iot.gateway.rest.enabled=true",
						"--spring.cloud.stream.coap.binder.mode=OUTBOUND_GATEWAY",
//						"--spring.cloud.stream.bindings.iotGatewayServer.binder=coap",
//						"--spring.cloud.stream.bindings.iotGatewayServer.producer.useNativeEncoding=true",
						"--spring.cloud.stream.bindings.iotGatewayClient.binder=coap",
						"--spring.cloud.stream.bindings.iotGatewayClient.producer.useNativeEncoding=true" });

		RestGatewayService restGatewayService = context.getBean(RestGatewayService.class);
		assertThat(restGatewayService, notNullValue());
		String body = restGatewayService.execute(new RestGatewayServiceRequest("http://example.com")).getBody();
		assertThat(body, containsString("Example Domain"));
		context.close();
	}

	@Configuration
	@EnableIotGatewayClient
	@EnableIotGatewayServer
	@EnableIntegration
	protected static class Config1 {
	}

	@Configuration
	protected static class BeanConfig {

		@Autowired
		RestGatewayService restGatewayService;
	}

	@Override
	protected AnnotationConfigApplicationContext buildContext() {
		return new AnnotationConfigApplicationContext();
	}
}

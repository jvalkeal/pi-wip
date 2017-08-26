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

import org.junit.Ignore;
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

@Ignore
public class RestGatewayServiceTests extends AbstractGatewayTests {

	@Test
	public void testAutowire() {
		SpringApplication app = new SpringApplication(ClientConfig.class, BeanConfig.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		ConfigurableApplicationContext context = app.run(new String[] { "--spring.cloud.iot.gateway.rest.enabled=true",
				"--spring.cloud.stream.bindings.iotGatewayClient.binder=coap" });
		context.close();
	}

	@Test
	public void testGateway() {

		SpringApplication serverApp = new SpringApplication(ServerConfig.class);
		serverApp.setWebApplicationType(WebApplicationType.NONE);
		ConfigurableApplicationContext serverContext = serverApp
				.run(new String[] { "--spring.cloud.iot.gateway.rest.enabled=true",
						"--spring.cloud.stream.coap.binder.mode=INBOUND_GATEWAY",
						"--spring.cloud.stream.bindings.iotGatewayServer.binder=coap",
						"--spring.cloud.stream.bindings.iotGatewayServer.producer.useNativeEncoding=true" });

		SpringApplication clientApp = new SpringApplication(ClientConfig.class);
		clientApp.setWebApplicationType(WebApplicationType.NONE);
		ConfigurableApplicationContext clientContext = clientApp
				.run(new String[] { "--spring.cloud.iot.gateway.rest.enabled=true",
						"--spring.cloud.stream.coap.binder.mode=OUTBOUND_GATEWAY",
						"--spring.cloud.stream.coap.binder.uri=coap://localhost:5683/spring-integration-coap",
						"--spring.cloud.stream.bindings.iotGatewayClient.binder=coap",
						"--spring.cloud.stream.bindings.iotGatewayClient.producer.useNativeEncoding=true" });

		RestGatewayService restGatewayService = clientContext.getBean(RestGatewayService.class);
		assertThat(restGatewayService, notNullValue());

		String body = restGatewayService.execute(new RestGatewayServiceRequest("http://example.com")).getBody();
		assertThat(body, containsString("Example Domain"));

		body = restGatewayService.execute(new RestGatewayServiceRequest("http://example.com")).getBody();
		assertThat(body, containsString("Example Domain"));

		clientContext.close();
		serverContext.close();
	}

	@Configuration
	@EnableIotGatewayClient
	@EnableIntegration
	protected static class ClientConfig {
	}

	@Configuration
	@EnableIotGatewayServer
	@EnableIntegration
	protected static class ServerConfig {
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

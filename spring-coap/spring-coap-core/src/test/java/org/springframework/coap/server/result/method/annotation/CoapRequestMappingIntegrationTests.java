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
package org.springframework.coap.server.result.method.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.coap.annotation.CoapController;
import org.springframework.coap.annotation.CoapRequestMapping;
import org.springframework.coap.annotation.CoapResponseBody;
import org.springframework.coap.californium.EnableCalifornium;
import org.springframework.coap.server.CoapServer;
import org.springframework.coap.server.ServerCoapExchange;
import org.springframework.coap.server.config.EnableCoapFlux;
import org.springframework.coap.server.support.CoapServerRefreshListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Generic integration tests for {@link CoapRequestMapping}.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapRequestMappingIntegrationTests extends AbstractCoapRequestMappingIntegrationTests {

	@Test
	public void handleSimpleGet() throws Exception {
		String expected = "simplehello";
		assertThat(expected).isEqualTo(performGet("/simple", String.class));
	}

	@Test
	public void handleMethodArgument1() throws Exception {
		String expected = "OK";
		assertThat(expected).isEqualTo(performGet("/argumenttest1", String.class));
	}

	@Configuration
	@EnableCoapFlux
	@EnableCalifornium
	static class CoapConfig {

		@Bean
		public CoapServerRefreshListener coapServerRefreshListener(CoapServer coapServer) {
			return new CoapServerRefreshListener(coapServer);
		}
	}

	@CoapController
	private static class TestCoapController {

		@CoapRequestMapping(path = "/simple")
		@CoapResponseBody
		public String simpleRequestMapping() {
			return "simplehello";
		}

		@CoapRequestMapping(path = "/argumenttest1")
		@CoapResponseBody
		public String simpleRequestMappingWithArgument1(ServerCoapExchange exchange) {
			return exchange != null ? "OK" : "NULL";
		}

	}

	@Override
	protected ApplicationContext initApplicationContext() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(CoapConfig.class, TestCoapController.class);
		context.refresh();
		return context;
	}
}

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
package org.springframework.cloud.iot.integration.coap;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.springframework.cloud.iot.integration.coap.server.CoapServerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestCoapServerConfiguration {

	@Bean
	public CoapServerFactoryBean testCoapServer() {
		CoapServerFactoryBean factory = new CoapServerFactoryBean();
		List<Resource> coapResources = new ArrayList<>();
		coapResources.add(new TestResource1());
		factory.setCoapResources(coapResources);
		return factory;
	}

	private class TestResource1 extends CoapResource {

		public TestResource1() {
			super("testresource1");
		}

		@Override
		public void handleGET(CoapExchange exchange) {
			exchange.respond(ResponseCode.VALID, "hello".getBytes());
		}

		@Override
		public void handlePOST(CoapExchange exchange) {
			exchange.respond(ResponseCode.VALID, "hello".getBytes());
		}
	}

}

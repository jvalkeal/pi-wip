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
package org.springframework.cloud.iot.coap.server;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.cloud.iot.coap.AbstractCoapTests;
import org.springframework.cloud.iot.coap.annotation.CoapController;
import org.springframework.cloud.iot.coap.annotation.CoapRequestMapping;
import org.springframework.cloud.iot.coap.annotation.CoapResponseBody;
import org.springframework.cloud.iot.coap.californium.CaliforniumCoapServerFactory;
import org.springframework.cloud.iot.coap.californium.CoapTemplate;
import org.springframework.cloud.iot.coap.server.support.DispatcherHandler;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

public class CaliforniumCoapServerTests extends AbstractCoapTests {

	@Test
	public void testServerNoHandler() throws Exception {
		context.refresh();
		CaliforniumCoapServerFactory factory = new CaliforniumCoapServerFactory();

		DispatcherHandler dispatcherHandler = new DispatcherHandler();
		dispatcherHandler.setApplicationContext(context);
		Map<String, CoapHandler> mappings = new HashMap<>();
		mappings.put("testresource1", dispatcherHandler);
		factory.setHandlerMappings(mappings);

		CoapServer coapServer = factory.getCoapServer();
		coapServer.start();

		URI uri = new URI("coap", null, "localhost", 5683, "/testresource1", null, null);
		CoapTemplate template = new CoapTemplate();
		String object = template.getForObject(uri, String.class);
		assertThat(object, nullValue());
	}

	@Test
	public void testServer() throws Exception {
		context.register(ControllerConfig1.class, Config1.class);
		context.refresh();
		CaliforniumCoapServerFactory factory = new CaliforniumCoapServerFactory();

		DispatcherHandler dispatcherHandler = new DispatcherHandler();
		dispatcherHandler.setApplicationContext(context);
		Map<String, CoapHandler> mappings = new HashMap<>();
		mappings.put("testresource1", dispatcherHandler);
		factory.setHandlerMappings(mappings);

		CoapServer coapServer = factory.getCoapServer();
		coapServer.start();

		URI uri = new URI("coap", null, "localhost", 5683, "/testresource1/hello1", null, null);
		CoapTemplate template = new CoapTemplate();
		String object = template.getForObject(uri, String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("hello1"));
	}

	@Override
	protected AnnotationConfigApplicationContext buildContext() {
		return new AnnotationConfigApplicationContext();
	}

	private static class Config1 {

		@Bean
		public ServerCoapResponseResultHandler serverCoapResponseResultHandler() {
			return new ServerCoapResponseResultHandler();
		}

		@Bean
		public RequestMappingHandlerMapping requestMappingHandlerMapping() {
			return new RequestMappingHandlerMapping();
		}

		@Bean
		public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
			return new RequestMappingHandlerAdapter();
		}

	}

	@CoapController
	@CoapRequestMapping(path = "/testresource1")
	private static class ControllerConfig1 {

		@CoapRequestMapping(path = "/hello1")
		@CoapResponseBody
		public String hello1() {
			return "hello1";
		}

		@CoapRequestMapping(path = "/hello2")
		@CoapResponseBody
		public String hello2() {
			return "hello2";
		}
	}

	@CoapController
	@CoapRequestMapping(path = "/testresource2")
	private static class ControllerConfig2 {

		@CoapRequestMapping(path = "/hello4")
		@CoapResponseBody
		public String hello3() {
			return "hello3";
		}

		@CoapRequestMapping(path = "/hello4")
		@CoapResponseBody
		public String hello4() {
			return "hello4";
		}
	}

}

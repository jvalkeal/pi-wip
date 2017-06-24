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
import org.springframework.cloud.iot.coap.californium.CaliforniumCoapServerFactory;
import org.springframework.cloud.iot.coap.californium.CoapTemplate;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
		assertThat(object, notNullValue());
		assertThat(object, is("hello"));
	}
	
	@Override
	protected AnnotationConfigApplicationContext buildContext() {
		return new AnnotationConfigApplicationContext();
	}

//	private class TestCoapServerHandler1 implements CoapServerHandler {
//		@Override
//		public ServerCoapResponse handle(ServerCoapRequest request) {
//			GenericServerCoapResponse response = new GenericServerCoapResponse();
//			response.setRequestPayload("hello".getBytes());
//			return response;
//		}
//	}

}

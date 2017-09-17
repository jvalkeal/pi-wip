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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.junit.Test;
import org.springframework.cloud.iot.coap.AbstractCoapTests;
import org.springframework.cloud.iot.coap.annotation.CoapController;
import org.springframework.cloud.iot.coap.annotation.CoapObservable;
import org.springframework.cloud.iot.coap.annotation.CoapRequestMapping;
import org.springframework.cloud.iot.coap.annotation.CoapResponseBody;
import org.springframework.cloud.iot.coap.californium.CaliforniumCoapServerFactory;
import org.springframework.cloud.iot.coap.californium.CoapTemplate;
import org.springframework.cloud.iot.coap.server.result.method.annotation.CoapResponseBodyResultHandler;
import org.springframework.cloud.iot.coap.server.result.method.annotation.CoapRequestMappingHandlerAdapter;
import org.springframework.cloud.iot.coap.server.result.method.annotation.CoapRequestMappingHandlerMapping;
import org.springframework.cloud.iot.coap.server.support.DispatcherHandler;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import reactor.core.publisher.Flux;

public class CaliforniumCoapServerTests extends AbstractCoapTests {

	@Test
	public void testServerNoHandler() throws Exception {
		context.refresh();
		CaliforniumCoapServerFactory factory = new CaliforniumCoapServerFactory();

		DispatcherHandler dispatcherHandler = new DispatcherHandler();
		dispatcherHandler.setApplicationContext(context);

		CoapServer coapServer = factory.getCoapServer();
		coapServer.start();

		URI uri = new URI("coap", null, "localhost", 5683, "/testresource1", null, null);
		CoapTemplate template = new CoapTemplate();
		String object = template.getForObject(uri, String.class);
		assertThat(object, nullValue());
		coapServer.stop();
	}

	@Test
	public void testServer() throws Exception {
		context.register(ControllerConfig1.class, ControllerConfig2.class, ControllerConfig3.class, Config1.class);
		context.refresh();
		CaliforniumCoapServerFactory factory = new CaliforniumCoapServerFactory();

		DispatcherHandler dispatcherHandler = new DispatcherHandler();
		dispatcherHandler.setApplicationContext(context);

		factory.setHandlerMappingRoot(dispatcherHandler);

		CoapServer coapServer = factory.getCoapServer();
		coapServer.start();

		CoapTemplate template = new CoapTemplate();

		URI hello1Uri = new URI("coap", null, "localhost", 5683, "/testresource1/hello1", null, null);
		String object = template.getForObject(hello1Uri, String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("hello1"));

		URI hello2Uri = new URI("coap", null, "localhost", 5683, "/testresource1/hello2", null, null);
		object = template.getForObject(hello2Uri, String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("hello2"));

		URI hello3Uri = new URI("coap", null, "localhost", 5683, "/testresource2/hello3", null, null);
		object = template.getForObject(hello3Uri, String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("hello3"));

		URI hello4Uri = new URI("coap", null, "localhost", 5683, "/testresource2/hello4", null, null);
		object = template.getForObject(hello4Uri, String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("hello4"));

		URI hello5Uri = new URI("coap", null, "localhost", 5683, "/hello5", null, null);
		object = template.getForObject(hello5Uri, String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("hello5"));
		coapServer.stop();
	}

	@Override
	protected AnnotationConfigApplicationContext buildContext() {
		return new AnnotationConfigApplicationContext();
	}

	private static class Config1 {

		@Bean
		public CoapResponseBodyResultHandler coapResponseBodyResultHandler() {
			return new CoapResponseBodyResultHandler();
		}

		@Bean
		public CoapRequestMappingHandlerMapping requestMappingHandlerMapping() {
			return new CoapRequestMappingHandlerMapping();
		}

		@Bean
		public CoapRequestMappingHandlerAdapter requestMappingHandlerAdapter() {
			return new CoapRequestMappingHandlerAdapter();
		}

	}

	@CoapController
	@CoapRequestMapping(path = "/testresource1")
	public static class ControllerConfig1 {

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
	public static class ControllerConfig2 {

		@CoapRequestMapping(path = "/hello3")
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

	@CoapController
	public static class ControllerConfig3 {

		@CoapRequestMapping(path = "/hello5")
		@CoapResponseBody
		public String hello5() {
			return "hello5";
		}
	}

	@CoapController
//	@CoapRequestMapping(path = "/testresource4")
	public static class ControllerConfig4 {

//		@CoapRequestMapping(path = "/obs1")
		@CoapObservable(path = "/obs1")
		public Flux<String> obs1() {
			return Flux.interval(Duration.ofSeconds(1)).map(i -> {
				return Long.toString(i);
			});
		}
	}
}

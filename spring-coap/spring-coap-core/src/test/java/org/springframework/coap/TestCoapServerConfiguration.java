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
package org.springframework.coap;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.springframework.coap.californium.AbstractCaliforniumCoapResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestCoapServerConfiguration {

	@Bean
	public CoapServerFactoryBean testCoapServer() {
		CoapServerFactoryBean factory = new CoapServerFactoryBean();
		List<Resource> coapResources = new ArrayList<>();
		coapResources.add(new TestResource1());
		coapResources.add(new TestResource2());
		coapResources.add(new TestResource3());
		coapResources.add(new TestResource4());
		coapResources.add(new TestResourceEcho());
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

	private class TestResource2 extends AbstractCaliforniumCoapResource {

		public TestResource2() {
			super("testresource2");
		}

		@Override
		public void handleGET(CoapExchange exchange) {
			exchange.respond(ResponseCode.VALID, "hello2".getBytes());
		}

		@Override
		public void handlePOST(CoapExchange exchange) {
			exchange.respond(ResponseCode.VALID, "hello2".getBytes());
		}
	}

	private class TestResource3 extends AbstractCaliforniumCoapResource {

		public TestResource3() {
			super("testresource3");
			setObservable(true);
			setObserveType(Type.CON);
			Timer timer = new Timer();
			timer.schedule(new UpdateTask(), 2000, 1000);
		}

		@Override
		public void handleGET(CoapExchange exchange) {
			exchange.respond(ResponseCode.VALID, "hello2".getBytes());
		}

		private class UpdateTask extends TimerTask {
			@Override
			public void run() {
				changed();
			}
		}
	}

	private class TestResource4 extends AbstractCaliforniumCoapResource {

		public TestResource4() {
			super("testresource4");
		}

		@Override
		public void handlePOST(CoapExchange exchange) {
			StringBuilder buf = new StringBuilder();
			buf.append("echo:");
			byte[] payload = exchange.getRequestPayload();
			if (payload != null && payload.length > 0) {
				buf.append(new String(payload));
			}
			exchange.respond(ResponseCode.VALID, buf.toString());
		}
	}

	private class TestResourceEcho extends AbstractCaliforniumCoapResource {

		public TestResourceEcho() {
			super("echo");
		}

		@Override
		public void handleGET(CoapExchange exchange) {
			exchange.respond(ResponseCode.VALID, exchange.getRequestPayload());
		}

		@Override
		public void handlePOST(CoapExchange exchange) {
			exchange.respond(ResponseCode.VALID, exchange.getRequestPayload());
		}

		@Override
		public void handleDELETE(CoapExchange exchange) {
			exchange.respond(ResponseCode.VALID, exchange.getRequestPayload());
		}

		@Override
		public void handlePUT(CoapExchange exchange) {
			exchange.respond(ResponseCode.VALID, exchange.getRequestPayload());
		}
	}

}

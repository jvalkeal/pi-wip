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
package org.springframework.cloud.iot.boot.autoconfigure.coap;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.iot.coap.californium.CaliforniumCoapServerFactory;
import org.springframework.cloud.iot.coap.server.CoapServer;
import org.springframework.cloud.iot.coap.server.result.method.annotation.CoapResponseBodyResultHandler;
import org.springframework.cloud.iot.coap.server.result.method.annotation.CoapRequestMappingHandlerAdapter;
import org.springframework.cloud.iot.coap.server.result.method.annotation.CoapRequestMappingHandlerMapping;
import org.springframework.cloud.iot.coap.server.support.DispatcherHandler;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for californium coap
 * server.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
@ConditionalOnClass(org.eclipse.californium.core.CoapServer.class)
public class CaliforniumCoapServerAutoConfiguration {

	@Bean
	public CoapServer coapServer(CaliforniumCoapServerFactory californiumCoapServerFactory) {
		return californiumCoapServerFactory.getCoapServer();
	}

	@Bean
	public CaliforniumCoapServerFactory californiumCoapServerFactory(DispatcherHandler californiumDispatcherHandler) {
		CaliforniumCoapServerFactory factory = new CaliforniumCoapServerFactory();
		factory.setHandlerMappingRoot(californiumDispatcherHandler);
		return factory;
	}

	@Bean
	public CoapServerRefreshListener coapServerRefreshListener(CoapServer coapServer) {
		return new CoapServerRefreshListener(coapServer);
	}

	@Bean
	public DispatcherHandler californiumDispatcherHandler() {
		return new DispatcherHandler();
	}

	@Bean
	public CoapResponseBodyResultHandler califormiumCoapResponseBodyResultHandler() {
		return new CoapResponseBodyResultHandler();
	}

	@Bean
	public CoapRequestMappingHandlerMapping californiumRequestMappingHandlerMapping() {
		return new CoapRequestMappingHandlerMapping();
	}

	@Bean
	public CoapRequestMappingHandlerAdapter californiumRequestMappingHandlerAdapter() {
		return new CoapRequestMappingHandlerAdapter();
	}

	private class CoapServerRefreshListener implements ApplicationListener<ContextRefreshedEvent>, DisposableBean {

		private final CoapServer coapServer;

		public CoapServerRefreshListener(CoapServer coapServer) {
			this.coapServer = coapServer;
		}

		@Override
		public void onApplicationEvent(ContextRefreshedEvent event) {
			coapServer.start();
		}

		@Override
		public void destroy() throws Exception {
			coapServer.stop();
		}
	}
}

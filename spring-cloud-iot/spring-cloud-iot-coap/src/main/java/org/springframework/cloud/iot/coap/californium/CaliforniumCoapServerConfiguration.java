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
package org.springframework.cloud.iot.coap.californium;

import org.springframework.cloud.iot.coap.server.CoapServer;
import org.springframework.cloud.iot.coap.server.support.DispatcherHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaliforniumCoapServerConfiguration {

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
}

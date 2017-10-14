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
package org.springframework.coap.server.config;

import org.springframework.coap.server.result.method.annotation.CoapObservableHandlerAdapter;
import org.springframework.coap.server.result.method.annotation.CoapObservableHandlerMapping;
import org.springframework.coap.server.result.method.annotation.CoapObservableResultHandler;
import org.springframework.coap.server.result.method.annotation.CoapRequestMappingHandlerAdapter;
import org.springframework.coap.server.result.method.annotation.CoapRequestMappingHandlerMapping;
import org.springframework.coap.server.result.method.annotation.CoapResponseBodyResultHandler;
import org.springframework.coap.server.support.DispatcherHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DelegatingCoapFluxConfiguration {

	@Bean
	public DispatcherHandler coapDispatcherHandler() {
		return new DispatcherHandler();
	}

	@Bean
	public CoapResponseBodyResultHandler coapResponseBodyResultHandler() {
		return new CoapResponseBodyResultHandler();
	}

	@Bean
	public CoapRequestMappingHandlerMapping coapRequestMappingHandlerMapping() {
		return new CoapRequestMappingHandlerMapping();
	}

	@Bean
	public CoapRequestMappingHandlerAdapter coapRequestMappingHandlerAdapter() {
		return new CoapRequestMappingHandlerAdapter();
	}

	@Bean
	public CoapObservableResultHandler coapObservableResultHander() {
		return new CoapObservableResultHandler();
	}

	@Bean
	public CoapObservableHandlerMapping coapObservableHandlerMapping() {
		return new CoapObservableHandlerMapping();
	}

	@Bean
	public CoapObservableHandlerAdapter coapObservableHandlerAdapter() {
		return new CoapObservableHandlerAdapter();
	}

}

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.californium.core.server.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.coap.server.CoapHandler;
import org.springframework.cloud.iot.coap.server.CoapServer;
import org.springframework.cloud.iot.coap.server.CoapServerFactory;
import org.springframework.cloud.iot.coap.server.CoapServerHandler;
import org.springframework.cloud.iot.coap.server.ConfigurableCoapServerFactory;
import org.springframework.util.Assert;

/**
 * {@link CoapServerFactory} which can be used to create {@link CaliforniumCoapServer}s.
 *
 * @author Janne Valkealahti
 *
 */
public class CaliforniumCoapServerFactory implements ConfigurableCoapServerFactory {

	private static final Logger log = LoggerFactory.getLogger(CaliforniumCoapServerFactory.class);
	private int port = 5683;
	private List<Resource> coapResources = new ArrayList<>();
	private Map<String, CoapHandler> mappings;

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public CoapServer getCoapServer() {
		log.info("Creating coap server in port {}", port);
		org.eclipse.californium.core.CoapServer server = new org.eclipse.californium.core.CoapServer(port);
		server.add(coapResources.toArray(new Resource[0]));
		if (mappings != null) {
			for (Entry<String,CoapHandler> entry : mappings.entrySet()) {
				server.add(new CaliforniumCoapHandlerResource(entry.getKey(), entry.getValue()));
			}
		}
		return new CaliforniumCoapServer(server);
	}

	@Override
	public void setHandlerMappings(Map<String, CoapHandler> mappings) {
		Assert.notNull(mappings, "mappings must not be null");
		this.mappings = mappings;
	}

	/**
	 * Sets the coap resources.
	 *
	 * @param coapResources the new coap resources
	 */
	public void setCoapResources(List<Resource> coapResources) {
		Assert.notNull(coapResources, "coapResources must not be null");
		this.coapResources = coapResources;
	}
}

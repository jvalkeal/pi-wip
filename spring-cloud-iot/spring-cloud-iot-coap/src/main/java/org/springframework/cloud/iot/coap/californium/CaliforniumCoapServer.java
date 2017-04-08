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

import java.util.List;

import org.eclipse.californium.core.network.Endpoint;
import org.springframework.cloud.iot.coap.server.CoapServer;
import org.springframework.cloud.iot.coap.server.CoapServerException;

/**
 * {@link CoapServer} that can be used to control a californium coap server.
 *
 * @author Janne Valkealahti
 *
 */
public class CaliforniumCoapServer implements CoapServer {

	private final org.eclipse.californium.core.CoapServer server;

	public CaliforniumCoapServer(org.eclipse.californium.core.CoapServer server) {
		this.server = server;
		initialize();
	}

	@Override
	public void start() throws CoapServerException {
		server.start();
	}

	@Override
	public void stop() throws CoapServerException {
		server.stop();
	}

	@Override
	public int getPort() {
		List<Endpoint> endpoints = server.getEndpoints();
		for (Endpoint ep : endpoints) {
			return ep.getAddress().getPort();
		}
		return -1;
	}

	private void initialize() throws CoapServerException {

	}


}

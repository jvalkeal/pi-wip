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
package org.springframework.cloud.iot.coap;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.Resource;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.util.Assert;

/**
 * Factory bean building an instance of a Californium {@link CoapServer}.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapServerFactoryBean extends AbstractFactoryBean<CoapServer> {

	private Integer port;

	private List<Resource> coapResources = new ArrayList<>();

	@Override
	protected CoapServer createInstance() throws Exception {
		CoapServer server = null;
		if (port != null) {
			server = new CoapServer(port);
		} else {
			server = new CoapServer();
		}
		server.add(coapResources.toArray(new Resource[0]));
		server.start();
		return server;
	}

	@Override
	public Class<CoapServer> getObjectType() {
		return CoapServer.class;
	}

	@Override
	protected void destroyInstance(CoapServer instance) throws Exception {
		instance.destroy();
	}

	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(Integer port) {
		this.port = port;
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

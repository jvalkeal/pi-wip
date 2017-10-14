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
package org.springframework.cloud.iot.coap.server.support;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.iot.coap.server.CoapServer;
import org.springframework.cloud.iot.coap.server.CoapServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;

/**
 * Class which can be created into application context as bean and will listen
 * {@link ContextRefreshedEvent} to start associated {@link CoapServer} and stop
 * it via {@link DisposableBean}.
 * <p>
 * Effectively needed if CoapServer is created via
 * {@link CoapServerFactory#getCoapServer()} as instance is paused when returned
 * from a factory.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapServerRefreshListener implements ApplicationListener<ContextRefreshedEvent>, DisposableBean {

	private final CoapServer coapServer;

	/**
	 * Instantiates a new coap server refresh listener.
	 *
	 * @param coapServer the coap server
	 */
	public CoapServerRefreshListener(CoapServer coapServer) {
		Assert.notNull(coapServer, "coapServer cannot be null");
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

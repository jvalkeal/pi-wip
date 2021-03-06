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
package org.springframework.cloud.iot.integration.coap.dsl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.cloud.iot.coap.server.CoapServer;
import org.springframework.cloud.iot.integration.coap.inbound.CoapInboundGateway;
import org.springframework.integration.dsl.ComponentsRegistration;
import org.springframework.integration.dsl.MessageHandlerSpec;
import org.springframework.integration.dsl.MessagingGatewaySpec;

/**
 * A {@link MessageHandlerSpec} for {@link CoapInboundGateway}s.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapInboundGatewaySpec extends MessagingGatewaySpec<CoapInboundGatewaySpec, CoapInboundGateway>
		implements ComponentsRegistration {

	private final boolean expectReply;
	private CoapServer coapServer;

	public CoapInboundGatewaySpec(boolean expectReply) {
		super(null);
		this.expectReply = expectReply;
	}

	@Override
	public Map<Object, String> getComponentsToRegister() {
		return Collections.emptyMap();
	}

	@Override
	protected CoapInboundGateway doGet() {
		CoapInboundGateway gateway = new CoapInboundGateway(coapServer, expectReply);
		return gateway;
	}

	/**
	 * Set an instance of {@link CoapServer} to be used with gateway.
	 *
	 * @param coapServer the coap server
	 */
	public void coapServer(CoapServer coapServer) {
		this.coapServer = coapServer;
	}
}

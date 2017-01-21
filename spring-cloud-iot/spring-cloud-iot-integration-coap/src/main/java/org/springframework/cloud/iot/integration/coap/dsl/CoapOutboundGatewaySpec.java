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

import org.springframework.cloud.iot.integration.coap.client.CoapOperations;
import org.springframework.cloud.iot.integration.coap.outbound.CoapOutboundGateway;
import org.springframework.integration.dsl.ComponentsRegistration;
import org.springframework.integration.dsl.MessageHandlerSpec;

/**
 * A {@link MessageHandlerSpec} for {@link CoapOutboundGateway}s.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapOutboundGatewaySpec extends MessageHandlerSpec<CoapOutboundGatewaySpec, CoapOutboundGateway>
		implements ComponentsRegistration {

	CoapOutboundGatewaySpec(CoapOperations coapOperations) {
		this.target = new CoapOutboundGateway(coapOperations);
		this.target.setRequiresReply(true);
	}

	public CoapOutboundGatewaySpec expectedResponseType(Class<?> expectedResponseType) {
		this.target.setExpectedResponseType(expectedResponseType);
		return this;
	}

	@Override
	public Collection<Object> getComponentsToRegister() {
		return Collections.emptyList();
	}

}

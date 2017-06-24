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

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.springframework.cloud.iot.integration.coap.outbound.CoapOutboundGateway;
import org.springframework.expression.Expression;
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

	CoapOutboundGatewaySpec(URI url) {
		this.target = new CoapOutboundGateway(url);
	}

	CoapOutboundGatewaySpec(String uri) {
		this.target = new CoapOutboundGateway(uri);
	}

	CoapOutboundGatewaySpec(Expression uriExpression) {
		this.target = new CoapOutboundGateway(uriExpression);
	}

	public CoapOutboundGatewaySpec expectedResponseType(Class<?> expectedResponseType) {
		this.target.setExpectedResponseType(expectedResponseType);
		return this;
	}

	public CoapOutboundGatewaySpec requiresReply(boolean requiresReply) {
		this.target.setRequiresReply(requiresReply);
		return this;
	}

	public CoapOutboundGatewaySpec expectReply(boolean expectReply) {
		this.target.setExpectReply(expectReply);
		return this;
	}

	@Override
	public Map<Object, String> getComponentsToRegister() {
		return Collections.emptyMap();
	}

}

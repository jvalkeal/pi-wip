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
package org.springframework.cloud.iot.integration.xbee.dsl;

import org.springframework.cloud.iot.integration.xbee.outbound.XBeeOutboundGateway;
import org.springframework.integration.dsl.MessageHandlerSpec;

import com.digi.xbee.api.XBeeDevice;

public class XBeeOutboundGatewaySpec extends MessageHandlerSpec<XBeeOutboundGatewaySpec, XBeeOutboundGateway> {

	private final XBeeDevice xbeeDevice;

	public XBeeOutboundGatewaySpec(XBeeDevice xbeeDevice) {
		super();
		this.xbeeDevice = xbeeDevice;
	}

	@Override
	protected XBeeOutboundGateway doGet() {
		return new XBeeOutboundGateway(xbeeDevice);
	}
}
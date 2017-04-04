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

import org.springframework.cloud.iot.integration.xbee.inbound.XBeeInboundGateway;
import org.springframework.cloud.iot.xbee.XBeeReceiver;
import org.springframework.cloud.iot.xbee.XBeeSender;

/**
 * {@code XBee} components factory.
 *
 * @author Janne Valkealahti
 *
 */
public class XBee {

	/**
	 * Create an {@link XBeeInboundGatewaySpec} builder for request-reply gateway
	 * based on provided {@link XBeeReceiver}.
	 *
	 * @param xbeeReceiver the xbee receiver
	 * @return the XBeeInboundChannelAdapterSpec instance
	 */
	public static XBeeInboundGatewaySpec inboundGateway(XBeeReceiver xbeeReceiver) {
		return new XBeeInboundGatewaySpec(new XBeeInboundGateway(xbeeReceiver));
	}

	/**
	 * Create an {@link XBeeInboundChannelAdapterSpec} builder for one-way adapter
	 * based on provided {@link XBeeReceiver}.
	 *
	 * @param xbeeReceiver the xbee receiver
	 * @return the XBeeInboundChannelAdapterSpec instance
	 */
	public static XBeeInboundChannelAdapterSpec inboundChannelAdapter(XBeeReceiver xbeeReceiver) {
		return new XBeeInboundChannelAdapterSpec(xbeeReceiver);
	}

	/**
	 * Create an {@link XBeeOutboundGatewaySpec} builder for request-reply gateway
	 * based on provided {@link XBeeSender}.
	 *
	 * @param xbeeSender the xbee sender
	 * @return the XBeeInboundChannelAdapterSpec instance
	 */
	public static XBeeOutboundGatewaySpec outboundGateway(XBeeSender xbeeSender) {
		return new XBeeOutboundGatewaySpec(xbeeSender);
	}

	/**
	 * Create an {@link XBeeOutboundChannelAdapterSpec} builder for one-way adapter
	 * based on provided {@link XBeeSender}.
	 *
	 * @param xbeeSender the xbee sender
	 * @return the XBeeOutboundChannelAdapterSpec instance
	 */
	public static XBeeOutboundChannelAdapterSpec outboundChannelAdapter(XBeeSender xbeeSender) {
		return new XBeeOutboundChannelAdapterSpec(xbeeSender);
	}

	private XBee() {
		super();
	}
}

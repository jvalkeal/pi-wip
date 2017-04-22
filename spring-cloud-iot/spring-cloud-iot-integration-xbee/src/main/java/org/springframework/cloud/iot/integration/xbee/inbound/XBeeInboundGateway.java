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
package org.springframework.cloud.iot.integration.xbee.inbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.xbee.XBeeReceiver;
import org.springframework.cloud.iot.xbee.listener.XBeeReceiverListener;
import org.springframework.integration.gateway.MessagingGatewaySupport;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

/**
 * Inbound gateway using XBee mesh network.
 *
 * @author Janne Valkealahti
 *
 */
public class XBeeInboundGateway extends MessagingGatewaySupport {


	private static final Logger log = LoggerFactory.getLogger(XBeeInboundGateway.class);
	private final XBeeReceiver xbeeReceiver;

	/**
	 * Instantiates a new xbee inbound gateway.
	 *
	 * @param xbeeReceiver the xbee receiver
	 */
	public XBeeInboundGateway(XBeeReceiver xbeeReceiver) {
		super();
		Assert.notNull(xbeeReceiver, "'xbeeReceiver' must be set");
		this.xbeeReceiver = xbeeReceiver;
	}

	@Override
	protected void onInit() throws Exception {
		super.onInit();
		setupListener();
	}

	@Override
	public String getComponentType() {
		return "xbee:inbound-gateway";
	}

	private void setupListener() {
		xbeeReceiver.addXBeeReceiverListener(new XBeeReceiverListener() {

			@Override
			public void onMessage(Message<byte[]> message) {
				log.debug("onMessage {}", message);
				send(message);
			}
		});
	}
}

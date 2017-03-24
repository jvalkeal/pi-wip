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

import org.springframework.cloud.iot.xbee.listener.XBeeReceiverListener;
import org.springframework.cloud.iot.xbee.support.DefaultXBeeComponent;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

import com.digi.xbee.api.XBeeDevice;

/**
 * Inbound channel adapter using XBee mesh network.
 *
 * @author Janne Valkealahti
 *
 */
public class XBeeInboundChannelAdapter extends MessageProducerSupport {

	private final DefaultXBeeComponent xBeeComponent;

	/**
	 * Instantiates a new xbee inbound channel adapter.
	 *
	 * @param xbeeDevice the xbee device
	 */
	public XBeeInboundChannelAdapter(XBeeDevice xbeeDevice) {
		super();
		Assert.notNull(xbeeDevice, "'xbeeDevice' must be set");
		this.xBeeComponent = new DefaultXBeeComponent(xbeeDevice);
	}

	@Override
	protected void onInit() {
		super.onInit();
		setupListener();
	}

	private void setupListener() {
		xBeeComponent.addXBeeReceiverListener(new XBeeReceiverListener() {

			@Override
			public void onMessage(Message<byte[]> message) {
				logger.info("DATA: " + new String(message.getPayload()));
				sendMessage(message);
			}
		});
	}

}

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

import org.springframework.integration.gateway.MessagingGatewaySupport;
import org.springframework.util.Assert;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;

/**
 * Inbound gateway using XBee mesh network.
 *
 * @author Janne Valkealahti
 *
 */
public class XBeeInboundGateway extends MessagingGatewaySupport {

	private final XBeeDevice xbeeDevice;

	/**
	 * Instantiates a new xbee inbound gateway.
	 *
	 * @param xbeeDevice the xbee device
	 */
	public XBeeInboundGateway(XBeeDevice xbeeDevice) {
		super();
		Assert.notNull(xbeeDevice, "'xbeeDevice' must be set");
		this.xbeeDevice = xbeeDevice;
	}

	@Override
	protected void onInit() throws Exception {
		super.onInit();
		setupListener();
	}

	private void setupListener() {
		xbeeDevice.addDataListener(new IDataReceiveListener() {

			@Override
			public void dataReceived(XBeeMessage xbeeMessage) {
				send(xbeeMessage.getData());
			}
		});
	}

}
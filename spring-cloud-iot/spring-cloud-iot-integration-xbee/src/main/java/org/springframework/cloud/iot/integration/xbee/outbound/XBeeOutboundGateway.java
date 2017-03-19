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
package org.springframework.cloud.iot.integration.xbee.outbound;

import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.util.Assert;

import com.digi.xbee.api.XBeeDevice;

/**
 * Outbound gateway using XBee mesh network.
 *
 * @author Janne Valkealahti
 *
 */
public class XBeeOutboundGateway extends AbstractReplyProducingMessageHandler {

	private final XBeeDevice xbeeDevice;

	/**
	 * Instantiates a new xbee outbound gateway.
	 *
	 * @param xbeeDevice the xbee device
	 */
	public XBeeOutboundGateway(XBeeDevice xbeeDevice) {
		super();
		Assert.notNull(xbeeDevice, "'xbeeDevice' must be set");
		this.xbeeDevice = xbeeDevice;
	}

	@Override
	protected Object handleRequestMessage(Message<?> requestMessage) {
		byte[] data = null;

		Object payload = requestMessage.getPayload();
		if (payload instanceof String) {
			data = ((String)payload).getBytes();
		} else if (payload instanceof byte[]) {
			data = (byte[])payload;
		}

		if (data != null && data.length > 0) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Broadcasting device='" + xbeeDevice + "' data='" + data + "' length='" + data.length
							+ "'");
				}
				xbeeDevice.sendBroadcastData(data);
				if (logger.isDebugEnabled()) {
					logger.debug("Broadcasting device='" + xbeeDevice + "' done");
				}
			} catch (Exception e) {
				logger.debug("Broadcasting device='" + xbeeDevice + "' failed", e);
				throw new MessageHandlingException(requestMessage, "Unable to send message", e);
			}
		}
		return null;
	}

}

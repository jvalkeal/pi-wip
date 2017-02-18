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

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;

public class XBeeOutboundGateway extends AbstractReplyProducingMessageHandler {

	private XBeeDevice xbeeDevice;

	public XBeeOutboundGateway(XBeeDevice xbeeDevice) {
		super();
		this.xbeeDevice = xbeeDevice;
	}

	@Override
	protected Object handleRequestMessage(Message<?> requestMessage) {
		try {
			Object payload = requestMessage.getPayload();
			logger.info("Handling message " + requestMessage);
			if (payload instanceof String) {
				logger.info("Broadcast " + payload);
				byte[] data = ((String)payload).getBytes();
				xbeeDevice.sendBroadcastData(data);
			} else if (payload instanceof byte[]) {
				logger.info("Broadcast " + payload);
				xbeeDevice.sendBroadcastData((byte[])payload);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

}

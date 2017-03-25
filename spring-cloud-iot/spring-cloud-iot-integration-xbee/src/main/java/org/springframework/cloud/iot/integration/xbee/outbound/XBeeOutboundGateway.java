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

import org.springframework.cloud.iot.xbee.XBeeSender;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

/**
 * Outbound gateway using XBee mesh network.
 *
 * @author Janne Valkealahti
 *
 */
public class XBeeOutboundGateway extends AbstractReplyProducingMessageHandler {

	private final XBeeSender xbeeSender;

	/**
	 * Instantiates a new xbee outbound gateway.
	 *
	 * @param xbeeSender the xbee senser
	 */
	public XBeeOutboundGateway(XBeeSender xbeeSender) {
		super();
		Assert.notNull(xbeeSender, "'xbeeSender' must be set");
		this.xbeeSender = xbeeSender;
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
			xbeeSender.sendMessage(MessageBuilder.withPayload(data).build());
		}
		return null;
	}

}

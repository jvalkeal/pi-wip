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
import org.springframework.cloud.iot.xbee.XBeeSender;
import org.springframework.cloud.iot.xbee.listener.XBeeReceiverListener;
import org.springframework.integration.gateway.MessagingGatewaySupport;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Inbound gateway using XBee mesh network.
 *
 * @author Janne Valkealahti
 *
 */
public class XBeeInboundGateway extends MessagingGatewaySupport {

	private static final Logger log = LoggerFactory.getLogger(XBeeInboundGateway.class);
	private final XBeeReceiver xbeeReceiver;
	private final XBeeSender xbeeSender;

	/**
	 * Instantiates a new xbee inbound gateway.
	 *
	 * @param xbeeReceiver the xbee receiver
	 */
	public XBeeInboundGateway(XBeeReceiver xbeeReceiver) {
		this(xbeeReceiver, null);
	}

	/**
	 * Instantiates a new xbee inbound gateway.
	 *
	 * @param xbeeReceiver the xbee receiver
	 * @param xbeeSender the xbee sender
	 */
	public XBeeInboundGateway(XBeeReceiver xbeeReceiver, XBeeSender xbeeSender) {
		super();
		Assert.notNull(xbeeReceiver, "'xbeeReceiver' must be set");
		this.xbeeReceiver = xbeeReceiver;
		this.xbeeSender = xbeeSender;
	}

	@Override
	protected void onInit() throws Exception {
		super.onInit();
		setupListener();
	}

	@Override
	public String getComponentType() {
		return xbeeSender != null ? "xbee:inbound-gateway" : "xbee:inbound-channel-adapter";
	}

	private void sendReplyMessage(Message<?> replyMessage) {
		log.debug("sendReplyMessage {}", replyMessage);
		byte[] data = null;

		Object payload = replyMessage.getPayload();
		if (payload instanceof String) {
			data = ((String)payload).getBytes();
		} else if (payload instanceof byte[]) {
			data = (byte[])payload;
		} else {
			throw new MessagingException("Request payload not String or byte[], was " + ClassUtils.getUserClass(payload));
		}

		if (data != null && data.length > 0) {
			Message<byte[]> message = MessageBuilder.withPayload(data).build();
			log.debug("Sending message {}", message);
			xbeeSender.sendMessage(message);
		}
	}

	private void setupListener() {
		xbeeReceiver.addXBeeReceiverListener(new XBeeReceiverListener() {

			@Override
			public void onMessage(Message<byte[]> message) {
				log.debug("onMessage {}", message);
//				send(message);
				Message<?> response = sendAndReceiveMessage(message);
				sendReplyMessage(response);
			}
		});
	}
}

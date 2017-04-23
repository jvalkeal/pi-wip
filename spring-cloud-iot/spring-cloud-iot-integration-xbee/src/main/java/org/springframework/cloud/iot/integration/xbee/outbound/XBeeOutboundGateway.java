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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.xbee.XBeeReceiver;
import org.springframework.cloud.iot.xbee.XBeeSender;
import org.springframework.cloud.iot.xbee.listener.XBeeReceiverListener;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.concurrent.SettableListenableFuture;

/**
 * Outbound gateway using XBee mesh network.
 *
 * @author Janne Valkealahti
 *
 */
public class XBeeOutboundGateway extends AbstractReplyProducingMessageHandler {

	private static final Logger log = LoggerFactory.getLogger(XBeeOutboundGateway.class);
	private final XBeeSender xbeeSender;
	private final XBeeReceiver xbeeReceiver;

	/**
	 * Instantiates a new xbee outbound gateway.
	 *
	 * @param xbeeSender the xbee senser
	 */
	public XBeeOutboundGateway(XBeeSender xbeeSender) {
		this(xbeeSender, null);
	}

	/**
	 * Instantiates a new x bee outbound gateway.
	 *
	 * @param xbeeSender the xbee sender
	 * @param xbeeReceiver the xbee receiver
	 */
	public XBeeOutboundGateway(XBeeSender xbeeSender, XBeeReceiver xbeeReceiver) {
		super();
		Assert.notNull(xbeeSender, "'xbeeSender' must be set");
		this.xbeeSender = xbeeSender;
		this.xbeeReceiver = xbeeReceiver;
	}

	@Override
	protected Object handleRequestMessage(Message<?> requestMessage) {
		log.debug("handleRequestMessage {}", requestMessage);
		byte[] data = null;

		Object payload = requestMessage.getPayload();
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
		MessageListenableFuture future = new MessageListenableFuture(xbeeReceiver);
		try {
			return future.get(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new MessagingException(requestMessage, e);
		} catch (ExecutionException e) {
			throw new MessagingException(requestMessage, e);
		} catch (TimeoutException e) {
			throw new MessagingException(requestMessage, e);
		}
	}

	@Override
	public String getComponentType() {
		return xbeeReceiver != null ? "xbee:outbound-gateway" : "xbee:outbound-channel-adapter";
	}

	private static class MessageListenableFuture extends SettableListenableFuture<Message<byte[]>> {

		public MessageListenableFuture(XBeeReceiver xbeeReceiver) {
			xbeeReceiver.addXBeeReceiverListener(new XBeeReceiverListener() {

				@Override
				public void onMessage(Message<byte[]> message) {
					set(message);
				}
			});
		}
	}
}

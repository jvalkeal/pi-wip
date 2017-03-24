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
package org.springframework.cloud.iot.xbee.support;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.xbee.XBeeReceiver;
import org.springframework.cloud.iot.xbee.XBeeSender;
import org.springframework.cloud.iot.xbee.listener.CompositeXBeeReceiverListener;
import org.springframework.cloud.iot.xbee.listener.XBeeReceiverListener;
import org.springframework.cloud.iot.xbee.protocol.RxMessageProtocol;
import org.springframework.cloud.iot.xbee.protocol.TxMessageProtocol;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;

/**
 * Default implementation of a {@link XBeeSender} and {@link XBeeReceiver}. This
 * implementation tries to provide easier way to work with a raw
 * {@link XBeeDevice} providing higher level transport protocol to overcome
 * XBee's limitations of a payload size per frame.
 *
 * @author Janne Valkealahti
 *
 */
public class DefaultXBeeComponent implements XBeeSender, XBeeReceiver {

	private static final Logger log = LoggerFactory.getLogger(DefaultXBeeComponent.class);

	private final XBeeDevice xbeeDevice;
	private final CompositeXBeeReceiverListener receiverListener = new CompositeXBeeReceiverListener();
	private final Map<String, RxMessageProtocol> rxSessions = new HashMap<>();
	private final Map<String, TxMessageProtocol> txSessions = new HashMap<>();
	private final XBeeIDataReceiveListener xbeeDataListener = new XBeeIDataReceiveListener();

	public DefaultXBeeComponent(XBeeDevice xbeeDevice) {
		this.xbeeDevice = xbeeDevice;
		this.xbeeDevice.addDataListener(xbeeDataListener);
	}

	@Override
	public void sendMessage(Message<byte[]> message) {
		// create session which handles sending this message
		TxMessageProtocol tx = new TxMessageProtocol(message.getPayload(), (short) 0);
		for (byte[] frame : tx.getFrames()) {
			try {
				log.debug("Broadcasting device='{}' frame='{}'", xbeeDevice, frame);
				xbeeDevice.sendBroadcastData(frame);
				log.debug("Broadcasting device='{}' done", xbeeDevice);
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (XBeeException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void addXBeeReceiverListener(XBeeReceiverListener listener) {
		receiverListener.register(listener);
	}

	@Override
	public void removeXBeeReceiverListener(XBeeReceiverListener listener) {
		receiverListener.unregister(listener);
	}

	private class XBeeIDataReceiveListener implements IDataReceiveListener {

		@Override
		public void dataReceived(XBeeMessage xbeeMessage) {
			byte[] data = xbeeMessage.getData();
			String deviceID = xbeeMessage.getDevice().get64BitAddress().generateDeviceID();
			int id = data[1] & 0xFF;
			String key = deviceID + id;
			RxMessageProtocol rxMessageSession = rxSessions.get(key);
			if (rxMessageSession == null) {
				rxMessageSession = new RxMessageProtocol();
				rxSessions.put(key, rxMessageSession);
			}

			log.debug("Adding data {}", data);
			boolean completed = rxMessageSession.add(data);
			log.debug("Protocol completed={}", completed);
			if (completed) {
				byte[] full = rxMessageSession.getData();
				log.debug("Full frame {}", full);
				receiverListener.onMessage(MessageBuilder.withPayload(full).build());
			}
		}
	}
}

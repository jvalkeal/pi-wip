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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.xbee.XBeeReceiver;
import org.springframework.cloud.iot.xbee.XBeeSender;
import org.springframework.messaging.Message;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.listeners.IModemStatusReceiveListener;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.models.ModemStatusEvent;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.packet.XBeePacket;

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

	public DefaultXBeeComponent(XBeeDevice xbeeDevice) {
		this.xbeeDevice = xbeeDevice;
	}

	@Override
	public void send(Message<?> message) {
	}

	private void xxx() {
		xbeeDevice.addDataListener(new IDataReceiveListener() {

			@Override
			public void dataReceived(XBeeMessage xbeeMessage) {
				log.debug("dataReceived {}", xbeeMessage);
			}
		});

		xbeeDevice.addIOSampleListener(new IIOSampleReceiveListener() {

			@Override
			public void ioSampleReceived(RemoteXBeeDevice remoteDevice, IOSample ioSample) {
				log.debug("ioSampleReceived {} {}", remoteDevice, ioSample);
			}
		});

		xbeeDevice.addModemStatusListener(new IModemStatusReceiveListener() {

			@Override
			public void modemStatusEventReceived(ModemStatusEvent modemStatusEvent) {
				log.debug("modemStatusEventReceived {}", modemStatusEvent);
			}
		});

		xbeeDevice.addPacketListener(new IPacketReceiveListener() {

			@Override
			public void packetReceived(XBeePacket receivedPacket) {
				log.debug("packetReceived {}", receivedPacket);
			}
		});
	}
}

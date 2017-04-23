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

import org.springframework.cloud.iot.xbee.protocol.RxMessageProtocol;
import org.springframework.cloud.iot.xbee.protocol.TxMessageProtocol;
import org.springframework.messaging.Message;

/**
 * Utility class tracking existing sessions for transferring and receiving sides.
 *
 * @author Janne Valkealahti
 *
 */
public class MessageProtocolSessions {

	private final Map<String, RxMessageProtocol> rxSessions = new HashMap<>();
	private final Map<String, TxMessageProtocol> txSessions = new HashMap<>();

	public TxMessageProtocol createTxSession(Message<byte[]> message) {
		String header = "iotGatewayServiceRoute:RestGatewayService";
		TxMessageProtocol tx = new TxMessageProtocol(header.getBytes(), message.getPayload(), (short) 0);
		return tx;
	}

	public RxMessageProtocol getRxSession(byte[] data, String deviceId) {
		int id = data[1] & 0xFF;
		String key = deviceId + id;
		RxMessageProtocol rxMessageSession = rxSessions.get(key);
		if (rxMessageSession == null) {
			rxMessageSession = new RxMessageProtocol();
			rxSessions.put(key, rxMessageSession);
		}
		return rxMessageSession;
	}

	public static class Key {

		private final int sessionId;
		private final String sessionAddress;

		public Key(int sessionId, String sessionAddress) {
			this.sessionId = sessionId;
			this.sessionAddress = sessionAddress;
		}

	}
}

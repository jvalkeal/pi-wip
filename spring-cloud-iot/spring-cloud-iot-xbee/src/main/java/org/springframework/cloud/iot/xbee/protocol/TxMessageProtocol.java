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
package org.springframework.cloud.iot.xbee.protocol;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.springframework.cloud.iot.support.IotUtils;

/**
 * {@code TxMessageProtocol} is a transmission implementation of a
 * {@link MessageProtocol}.
 *
 * @author Janne Valkealahti
 * @see MessageProtocol
 *
 */
public class TxMessageProtocol extends MessageProtocol {

	private final byte[] data;
	private final short sessionId;

	public TxMessageProtocol(byte[] data, short sessionId) {
		this.data = data;
		this.sessionId = sessionId;
	}

	public byte[][] getFrames() {
		int frameLength = getFrameSize() + 4;
		int frameCount = (data.length / frameLength) + 1;
		byte[][] frames = new byte[frameCount][];
		for (int i = 0; i < frameCount; i++) {
			int messageType = 0x00;
			if (i == 0) {
				messageType = IotUtils.setBit(messageType, MessageProtocol.MESSAGE_TYPE_START);
			}
			if (i == (frameCount - 1)) {
				messageType = IotUtils.setBit(messageType, MessageProtocol.MESSAGE_TYPE_END);
			}
			ByteBuffer buffer = ByteBuffer.allocate(frameLength)
				.put((byte) messageType)
				.put((byte) sessionId)
				.putShort((short) i)
				.put(Arrays.copyOfRange(data, i * getFrameSize(), Math.min((i+1) * getFrameSize(), data.length)));
			byte[] b = new byte[buffer.position()];
			buffer.flip();
			buffer.get(b);
			frames[i] = b;
		}

		return frames;
	}

}

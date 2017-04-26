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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.support.HexUtils;
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

	private static final Logger log = LoggerFactory.getLogger(TxMessageProtocol.class);
	private final byte[] header;
	private final byte[] payload;
	private final short sessionId;

	/**
	 * Instantiates a new tx message protocol.
	 *
	 * @param payload the payload data
	 * @param sessionId the session id
	 */
	public TxMessageProtocol(byte[] payload, short sessionId) {
		this(null, payload, sessionId);
	}

	/**
	 * Instantiates a new tx message protocol.
	 *
	 * @param header the header data
	 * @param payload the payload data
	 * @param sessionId the session id
	 */
	public TxMessageProtocol(byte[] header, byte[] payload, short sessionId) {
		super(DEFAULT_FRAME_SIZE);
		this.header = header != null ? header : new byte[0];
		this.payload = payload != null ? payload : new byte[0];
		this.sessionId = sessionId;
	}

	/**
	 * Build array of frames which are split by a frame size.
	 *
	 * @return the array of frames
	 */
	public byte[][] getFrames() {
		int frameLength = getFrameSize();
		int frameCount = calculateFrameCount();
		byte[] tmp = IotUtils.concat(header, payload);
		byte[][] frames = new byte[frameCount][];
		int position = 0;
		for (int i = 0; i < frameCount; i++) {
			int messageType = 0x00;
			if (i == 0) {
				messageType = IotUtils.setBit(messageType, MessageProtocol.MESSAGE_TYPE_START);
			}
			if (i == (frameCount - 1)) {
				messageType = IotUtils.setBit(messageType, MessageProtocol.MESSAGE_TYPE_END);
			}
			ByteBuffer buffer = ByteBuffer
					.allocate(frameLength)
					.put((byte) messageType)
					.put((byte) sessionId).putShort((short) i);
			if (i == 0) {
				buffer.putInt(header.length);
			}
			int takeLength = frameLength - (i == 0 ? 8 : 4);
			byte[] bytes = Arrays.copyOfRange(tmp, position, Math.min(position + takeLength, tmp.length));
			log.debug("Construct frame {} as {} \n{}", i, bytes, HexUtils.prettyHexDump(bytes));
			buffer.put(bytes);
			byte[] b = new byte[buffer.position()];
			buffer.flip();
			buffer.get(b);
			frames[i] = b;
			position += takeLength;
		}
		return frames;
	}

	private int calculateFrameCount() {
		// first package has 8 byte header, so need to add 4
		int total = payload.length + header.length + 4;
		int frameCount = total / (getFrameSize() - 4);
		frameCount += 1;
		return frameCount;
	}
}

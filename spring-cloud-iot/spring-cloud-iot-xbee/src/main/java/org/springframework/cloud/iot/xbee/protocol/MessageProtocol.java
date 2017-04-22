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

/**
 * Base class for message protocol which works on top of XBee's raw packet level
 * transport.
 * <p>
 * {@code MessageProtocol} is meant to overcome frame transport limitations in
 * XBee devices which imposes hard limit how many bytes can be transferred per
 * one frame. Depending on a device in use this limit changes anything from 80
 * bytes to 256 bytes. This protocol give freedom to send larger payloads which
 * will be disassembled on a transferring side and then reassembled on a
 * receiving side.
 * <p>
 * Every frame has its frame headers which consist of message type, session id, frame id and
 * optionally a message header length if message type has {@code MESSAGE_TYPE_START} bit set.
 * <p>
 * Frame has a structure as:
 * <pre>
 * byte 0 message type (byte)
 * byte 1 message session id (byte)
 * bytes 2-3 message frame id (short)
 * bytes 4-7 header length if {@code MESSAGE_TYPE_START} is set (int)
 * </pre>
 *
 * @author Janne Valkealahti
 *
 */
public abstract class MessageProtocol {

	public final static int DEFAULT_FRAME_SIZE = 60;
	public final static int MESSAGE_TYPE_START = 0x01;
	public final static int MESSAGE_TYPE_END = 0x02;
	private final int frameSize;

	/**
	 * Instantiates a new message protocol.
	 *
	 * @param frameSize the frame size
	 */
	public MessageProtocol(int frameSize) {
		this.frameSize = frameSize;
	}

	/**
	 * Gets the frame size.
	 *
	 * @return the frame size
	 */
	public int getFrameSize() {
		return frameSize;
	}
}

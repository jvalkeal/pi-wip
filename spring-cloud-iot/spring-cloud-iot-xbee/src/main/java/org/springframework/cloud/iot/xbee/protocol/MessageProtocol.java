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
 *
 * @author Janne Valkealahti
 *
 */
public abstract class MessageProtocol {

	public final static int MESSAGE_TYPE_START = 0x01;
	public final static int MESSAGE_TYPE_END = 0x02;
	// 0    message type
	// 1    message session id
	// 2-3  message order

	private int frameSize = 60;

	public int getFrameSize() {
		return frameSize;
	}

}

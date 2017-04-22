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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.support.IotUtils;

/**
 * {@code RxMessageProtocol} is a receiving implementation of a
 * {@link MessageProtocol}.
 *
 * @author Janne Valkealahti
 * @see MessageProtocol
 *
 */
public class RxMessageProtocol extends MessageProtocol {

	private static final Logger log = LoggerFactory.getLogger(RxMessageProtocol.class);
	private final Map<Integer, byte[]> frames = new HashMap<>();
	private int headerLength = 0;

	/**
	 * Instantiates a new rx message protocol.
	 */
	public RxMessageProtocol() {
		super(DEFAULT_FRAME_SIZE);
	}

	/**
	 * Add a new frame data and return {@code true} if receiving side if fully
	 * constructed. After full construction {@link #getHeader()} and
	 * {@link #getPayload()} will provide valid data.
	 *
	 * @param frame the frame
	 * @return true, if successful
	 */
	public boolean add(byte[] frame) {
		log.debug("Adding new frame '{}'", frame);
		short id = ByteBuffer.wrap(new byte[] { frame[2], frame[3] }).getShort();
		if (IotUtils.isBitSet(frame[0], MessageProtocol.MESSAGE_TYPE_START)) {
			headerLength = ByteBuffer.wrap(frame, 4, 4).getInt();
			frames.put((int)id, Arrays.copyOfRange(frame, 8, frame.length));
		} else {
			frames.put((int)id, Arrays.copyOfRange(frame, 4, frame.length));
		}
		return IotUtils.isBitSet(frame[0], MessageProtocol.MESSAGE_TYPE_END);
	}

	/**
	 * Gets the payload
	 *
	 * @return the payload
	 */
	public byte[] getPayload() {
		List<byte[]> dataFrames = frames.entrySet()
				.stream()
				.sorted(Map.Entry.<Integer, byte[]>comparingByKey()).map(e -> e.getValue())
				.collect(Collectors.toList());
		byte[] tmp = concatenateByteArrays(dataFrames);
		return Arrays.copyOfRange(tmp, headerLength, tmp.length);
	}

	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public byte[] getHeader() {
		List<byte[]> dataFrames = frames.entrySet()
				.stream()
				.sorted(Map.Entry.<Integer, byte[]>comparingByKey()).map(e -> e.getValue())
				.collect(Collectors.toList());
		byte[] tmp = concatenateByteArrays(dataFrames);
		return Arrays.copyOfRange(tmp, 0, headerLength);
	}

	private static byte[] concatenateByteArrays(List<byte[]> dataFrames) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (byte[] df : dataFrames) {
			out.write(df, 0, df.length);
		}
		return out.toByteArray();
	}
}

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

	public boolean add (byte[] frame) {
		log.debug("Adding frame '{}'", frame);
		short id = ByteBuffer.wrap(new byte[] { frame[2], frame[3] }).getShort();
		frames.put((int)id, Arrays.copyOfRange(frame, 4, frame.length));
		return IotUtils.isBitSet(frame[0], MessageProtocol.MESSAGE_TYPE_END);
	}

	public byte[] getData() {
		List<byte[]> dataFrames = frames.entrySet()
				.stream()
				.sorted(Map.Entry.<Integer, byte[]>comparingByKey()).map(e -> e.getValue())
				.collect(Collectors.toList());
		return concatenateByteArrays(dataFrames);
	}

	private static byte[] concatenateByteArrays(List<byte[]> dataFrames) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (byte[] df : dataFrames) {
			out.write(df, 0, df.length);
		}
		return out.toByteArray();
	}
}

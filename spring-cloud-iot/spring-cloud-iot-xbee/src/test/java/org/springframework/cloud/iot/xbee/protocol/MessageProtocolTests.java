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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.cloud.iot.support.IotUtils;

public class MessageProtocolTests {

	private final static String DATA1 =
			"0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" +
			"0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789";

	@Test
	public void testSplitTwoFrames() {
		TxMessageProtocol tx = new TxMessageProtocol(DATA1.getBytes(), (short) 0);
		byte[][] frames = tx.getFrames();

		assertThat(frames.length, is(2));
		assertThat(IotUtils.isBitSet(frames[0][0], MessageProtocol.MESSAGE_TYPE_START), is(true));
		assertThat(IotUtils.isBitSet(frames[0][0], MessageProtocol.MESSAGE_TYPE_END), is(false));
		assertThat(IotUtils.isBitSet(frames[1][0], MessageProtocol.MESSAGE_TYPE_START), is(false));
		assertThat(IotUtils.isBitSet(frames[1][0], MessageProtocol.MESSAGE_TYPE_END), is(true));

		RxMessageProtocol rx = new RxMessageProtocol();
		rx.add(frames[0]);
		rx.add(frames[1]);

		assertThat(new String(rx.getData()), is(DATA1));
	}

}

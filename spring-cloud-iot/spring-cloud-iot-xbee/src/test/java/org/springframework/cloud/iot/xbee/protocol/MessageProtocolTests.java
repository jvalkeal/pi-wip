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

	private final static String HEADER1 =
			"header";

	private final static String HEADER2 =
			"header" + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789";

	private final static String PAYLOAD1 =
			"0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" +
			"0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789";

	private final static String PAYLOAD2 =
			"0123456789";

	private final static String PAYLOAD3 =
			"0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" +
			"0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" +
			"0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" +
			"0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" +
			"0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" +
			"0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789";

	@Test
	public void testEmptyPayloadAndHeader() {
		TxMessageProtocol tx = new TxMessageProtocol(new byte[0], (short) 0);
		byte[][] frames = tx.getFrames();

		assertThat(frames.length, is(1));
		assertThat(IotUtils.isBitSet(frames[0][0], MessageProtocol.MESSAGE_TYPE_START), is(true));
		assertThat(IotUtils.isBitSet(frames[0][0], MessageProtocol.MESSAGE_TYPE_END), is(true));

		RxMessageProtocol rx = new RxMessageProtocol();
		rx.add(frames[0]);

		assertThat(new String(rx.getPayload()), is(""));
	}

	@Test
	public void testEmptySingleFrameNoHeader() {
		TxMessageProtocol tx = new TxMessageProtocol(PAYLOAD2.getBytes(), (short) 0);
		byte[][] frames = tx.getFrames();

		assertThat(frames.length, is(1));
		assertThat(IotUtils.isBitSet(frames[0][0], MessageProtocol.MESSAGE_TYPE_START), is(true));
		assertThat(IotUtils.isBitSet(frames[0][0], MessageProtocol.MESSAGE_TYPE_END), is(true));

		RxMessageProtocol rx = new RxMessageProtocol();
		rx.add(frames[0]);

		assertThat(new String(rx.getPayload()), is(PAYLOAD2));
	}

	@Test
	public void testEmptyMultiFrameNoHeader() {
		TxMessageProtocol tx = new TxMessageProtocol(PAYLOAD3.getBytes(), (short) 0);
		byte[][] frames = tx.getFrames();

		assertThat(frames.length, is(6));
		assertThat(IotUtils.isBitSet(frames[0][0], MessageProtocol.MESSAGE_TYPE_START), is(true));
		assertThat(IotUtils.isBitSet(frames[5][0], MessageProtocol.MESSAGE_TYPE_END), is(true));

		RxMessageProtocol rx = new RxMessageProtocol();
		rx.add(frames[0]);
		rx.add(frames[1]);
		rx.add(frames[2]);
		rx.add(frames[3]);
		rx.add(frames[4]);
		rx.add(frames[5]);

		assertThat(new String(rx.getPayload()), is(PAYLOAD3));
	}

	@Test
	public void testEmptySingleFrameWithHeader() {
		TxMessageProtocol tx = new TxMessageProtocol(HEADER1.getBytes(), PAYLOAD2.getBytes(), (short) 0);
		byte[][] frames = tx.getFrames();

		assertThat(frames.length, is(1));
		assertThat(IotUtils.isBitSet(frames[0][0], MessageProtocol.MESSAGE_TYPE_START), is(true));
		assertThat(IotUtils.isBitSet(frames[0][0], MessageProtocol.MESSAGE_TYPE_END), is(true));

		RxMessageProtocol rx = new RxMessageProtocol();
		rx.add(frames[0]);

		assertThat(new String(rx.getHeader()), is(HEADER1));
		assertThat(new String(rx.getPayload()), is(PAYLOAD2));
	}

	@Test
	public void testEmptySingleFrameWithLargeHeader() {
		TxMessageProtocol tx = new TxMessageProtocol(HEADER2.getBytes(), PAYLOAD2.getBytes(), (short) 0);
		byte[][] frames = tx.getFrames();

		assertThat(frames.length, is(2));
		assertThat(IotUtils.isBitSet(frames[0][0], MessageProtocol.MESSAGE_TYPE_START), is(true));
		assertThat(IotUtils.isBitSet(frames[1][0], MessageProtocol.MESSAGE_TYPE_END), is(true));

		RxMessageProtocol rx = new RxMessageProtocol();
		rx.add(frames[0]);
		rx.add(frames[1]);

		assertThat(new String(rx.getHeader()), is(HEADER2));
		assertThat(new String(rx.getPayload()), is(PAYLOAD2));
	}

	@Test
	public void testTwoFramesNoHeader() {
		TxMessageProtocol tx = new TxMessageProtocol(PAYLOAD1.getBytes(), (short) 0);
		byte[][] frames = tx.getFrames();

		assertThat(frames.length, is(2));
		assertThat(IotUtils.isBitSet(frames[0][0], MessageProtocol.MESSAGE_TYPE_START), is(true));
		assertThat(IotUtils.isBitSet(frames[0][0], MessageProtocol.MESSAGE_TYPE_END), is(false));
		assertThat(IotUtils.isBitSet(frames[1][0], MessageProtocol.MESSAGE_TYPE_START), is(false));
		assertThat(IotUtils.isBitSet(frames[1][0], MessageProtocol.MESSAGE_TYPE_END), is(true));

		RxMessageProtocol rx = new RxMessageProtocol();
		rx.add(frames[0]);
		rx.add(frames[1]);

		assertThat(new String(rx.getPayload()), is(PAYLOAD1));
	}

	@Test
	public void testLongMessageWithHeader() {
		byte[] headerIn = getFakeData(41);
		byte[] payloadIn = getFakeData(1348);
		TxMessageProtocol tx = new TxMessageProtocol(headerIn, payloadIn, (short) 0);
		byte[][] frames = tx.getFrames();
		assertThat(frames.length, is(25));

		RxMessageProtocol rx = new RxMessageProtocol();
		for (int i = 0; i < frames.length; i++) {
			rx.add(frames[i]);
		}
		byte[] headerOut = rx.getHeader();
		byte[] payloadOut = rx.getPayload();

		assertThat(headerIn.length, is(headerOut.length));
		assertThat(payloadIn.length, is(payloadOut.length));
	}

	private static byte[] getFakeData(int size) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < size; i++) {
			buf.append('X');
		}
		return buf.toString().getBytes();
	}

}

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
package org.springframework.cloud.iot.support;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class IotUtilsTests {

	@Test
	public void testIsBitSet() {
		// 0x7F 127 01111111
		// 0x80 128 10000000
		assertThat(IotUtils.isBitSet(0x7F, 0x80), is(false));
		// 0xC0 192 11000000
		assertThat(IotUtils.isBitSet(0x7F, 0xC0), is(false));
		// 0xC1 193 11000001
		assertThat(IotUtils.isBitSet(0xC0, 0xC1), is(true));
	}

	@Test
	public void testSetBit() {
		// 0x7F 127 01111111
		// 0x80 128 10000000
		// 0xFF 255 11111111
		assertThat(IotUtils.setBit(0x7F, 0x80), is(0xFF));
	}

	@Test
	public void testUnsetBit() {
		// 0x03 3   00000011
		// 0xFD 253 11111101
		// 0x01 1   00000001
		assertThat(IotUtils.unsetBit(0x03, 0xFD), is(0x01));
	}

}

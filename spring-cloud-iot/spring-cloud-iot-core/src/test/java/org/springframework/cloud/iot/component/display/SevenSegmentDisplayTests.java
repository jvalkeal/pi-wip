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
package org.springframework.cloud.iot.component.display;

import org.junit.Test;

public class SevenSegmentDisplayTests {

	// 8 0x08 00001000

	// 128 0x80 10000000
	// 8 0x7F on on on on on on on
	// 0x7F 127 01111111
	// 127 << 0 = 127
	// 127 << 1 = 254
	// 127 << 2 = 508
	// 127 << 3 = 1016
	// 127 << 4 = 2032
	// 127 << 5 = 4064
	// 127 << 6 = 8128
	// 127 << 7 = 16256

	// 1 0x01 00000001
	// 6 0x06 00000110
	// 7 0x07 00000111
	// 7 xor 1 = 6


//	128 0x80 10000000
//	64  0x40 01000000
//	32  0x20 00100000
//	16  0x10 00010000
//	8   0x08 00001000
//	4   0x04 00000100
//	2   0x02 00000010
//	1   0x01 00000001

	private final int[] segCode = new int[] { 0x3f, 0x06, 0x5b, 0x4f, 0x66, 0x6d, 0x7d, 0x07, 0x7f, 0x6f, 0x77, 0x7c, 0x39, 0x5e, 0x79,
			0x71, 0x80 };

	@Test
	public void test1() {
		for (int j = 0; j < segCode.length; j++) {
			for (int i = 0; i < 8; i++) {
				int x1 = (0x80 & (segCode[j] << i));
				int x2 = ((segCode[j] << i));
				System.out.println(j + " " + segCode[j] + " " + i + " " +  x1 + " " +  x2);
			}
		}

	}

	@Test
	public void test2() {
//		SevenSegmentDisplay d = new SevenSegmentDisplay();
//		d.setInt(8);
//		System.out.println(Character.getNumericValue('a'));
//		System.out.println(Character.getNumericValue('o'));
//		System.out.println(Character.getNumericValue('A'));
//		System.out.println(Character.getNumericValue('O'));
		int[] charMapping = new int[3];
		for (int j = 0; j < charMapping.length; j++) {
			System.out.println(charMapping[j]);
		}
	}

	@Test
	public void test3() {
		for (int i = 128; i >= 1; i/=2) {
			System.out.println(isFlagSet(i, 0x7F) ? "on" : "off" );
			System.out.println(i % 10);
		}
	}

	@Test
	public void test4() {
		int[] segCode2 = new int[] { 0x7F };
		for (int j = 0; j < segCode2.length; j++) {
			for (int i = 0; i < 8; i++) {
				int xxx = (0x80 & (segCode2[j] << i));
				System.out.println(xxx);
			}
		}
	}

	public static boolean isFlagSet(int value, int flags)
	{
		return (flags & value) == value;
	}

	public static byte setFlag(byte value, byte flags)
	{
		return (byte) (flags | value);
	}

	public static byte unsetFlag(byte value, byte flags)
	{
		return (byte) (flags & ~value);
	}

}

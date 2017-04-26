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

/**
 * HEX utilities to pretty print byte arrays.
 *
 * @author Janne Valkealahti
 *
 */
public class HexUtils {

	public static final String EMPTY_STRING = "";
	public static final String NEWLINE = "\n";
	private static final char[] BYTE2CHAR = new char[256];
	private static final char[] HEXDUMP_TABLE = new char[256 * 4];
	private static final String[] HEXPADDING = new String[16];
	private static final String[] HEXDUMP_ROWPREFIXES = new String[65536 >>> 4];
	private static final String[] BYTE2HEX = new String[256];
	private static final String[] BYTEPADDING = new String[16];
	private static final String[] BYTE2HEX_PAD = new String[256];
	private static final String[] BYTE2HEX_NOPAD = new String[256];

	static {
		// Generate the lookup table that converts a byte into a 2-digit
		// hexadecimal integer.
		int k;
		for (k = 0; k < 10; k++) {
			BYTE2HEX_PAD[k] = "0" + k;
			BYTE2HEX_NOPAD[k] = String.valueOf(k);
		}
		for (; k < 16; k++) {
			char c = (char) ('a' + k - 10);
			BYTE2HEX_PAD[k] = "0" + c;
			BYTE2HEX_NOPAD[k] = String.valueOf(c);
		}
		for (; k < BYTE2HEX_PAD.length; k++) {
			String str = Integer.toHexString(k);
			BYTE2HEX_PAD[k] = str;
			BYTE2HEX_NOPAD[k] = str;
		}

		final char[] DIGITS = "0123456789abcdef".toCharArray();
		for (int i = 0; i < 256; i++) {
			HEXDUMP_TABLE[i << 1] = DIGITS[i >>> 4 & 0x0F];
			HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i & 0x0F];
		}

		int i;

		// Generate the lookup table for hex dump paddings
		for (i = 0; i < HEXPADDING.length; i++) {
			int padding = HEXPADDING.length - i;
			StringBuilder buf = new StringBuilder(padding * 3);
			for (int j = 0; j < padding; j++) {
				buf.append("   ");
			}
			HEXPADDING[i] = buf.toString();
		}

		// Generate the lookup table for the start-offset header in each row (up
		// to 64KiB).
		for (i = 0; i < HEXDUMP_ROWPREFIXES.length; i++) {
			StringBuilder buf = new StringBuilder(12);
			buf.append(NEWLINE);
			buf.append(Long.toHexString(i << 4 & 0xFFFFFFFFL | 0x100000000L));
			buf.setCharAt(buf.length() - 9, '|');
			buf.append('|');
			HEXDUMP_ROWPREFIXES[i] = buf.toString();
		}

		// Generate the lookup table for byte-to-hex-dump conversion
		for (i = 0; i < BYTE2HEX.length; i++) {
			BYTE2HEX[i] = ' ' + byteToHexStringPadded(i);
		}

		// Generate the lookup table for byte dump paddings
		for (i = 0; i < BYTEPADDING.length; i++) {
			int padding = BYTEPADDING.length - i;
			StringBuilder buf = new StringBuilder(padding);
			for (int j = 0; j < padding; j++) {
				buf.append(' ');
			}
			BYTEPADDING[i] = buf.toString();
		}

		// Generate the lookup table for byte-to-char conversion
		for (i = 0; i < BYTE2CHAR.length; i++) {
			if (i <= 0x1f || i >= 0x7f) {
				BYTE2CHAR[i] = '.';
			} else {
				BYTE2CHAR[i] = (char) i;
			}
		}
	}

	/**
	 * Returns a netty style pretty print format of a byte array.
	 *
	 * @param array the byte array
	 * @return a pretty print style representation
	 */
	public static String prettyHexDump(byte[] array) {
		return prettyHexDump(array, 0, array.length);
	}

	/**
	 * Returns a netty style pretty print format of a byte array.
	 *
	 * @param array the byte array
	 * @param offset the offset to print
	 * @param length the length to take from array
	 * @return a pretty print style representation
	 */
	public static String prettyHexDump(byte[] array, int offset, int length) {
		if (length == 0) {
			return EMPTY_STRING;
		} else {
			int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
			StringBuilder buf = new StringBuilder(rows * 80);
			appendPrettyHexDump(buf, array, offset, length);
			return buf.toString();
		}
	}

	/**
	 * Converts the specified byte value into a 2-digit hexadecimal integer.
	 */
	private static String byteToHexStringPadded(int value) {
		return BYTE2HEX_PAD[value & 0xff];
	}

	private static void appendPrettyHexDump(StringBuilder dump, byte[] array, int offset, int length) {
		if (array.length > (offset + length)) {
			throw new IndexOutOfBoundsException(
					"Array length " + array.length + " but requested offset " + offset + " and length " + length);
		}
		if (length == 0) {
			return;
		}
		dump.append("         +-------------------------------------------------+" + NEWLINE
				+ "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |" + NEWLINE
				+ "+--------+-------------------------------------------------+----------------+");

		final int startIndex = offset;
		final int fullRows = length >>> 4;
		final int remainder = length & 0xF;

		// Dump the rows which have 16 bytes.
		for (int row = 0; row < fullRows; row++) {
			int rowStartIndex = (row << 4) + startIndex;

			// Per-row prefix.
			appendHexDumpRowPrefix(dump, row, rowStartIndex);

			// Hex dump
			int rowEndIndex = rowStartIndex + 16;
			for (int j = rowStartIndex; j < rowEndIndex; j++) {
				dump.append(BYTE2HEX[(int) array[j]]);
			}
			dump.append(" |");

			// ASCII dump
			for (int j = rowStartIndex; j < rowEndIndex; j++) {
				dump.append(BYTE2CHAR[(int) array[j]]);
			}
			dump.append('|');
		}

		// Dump the last row which has less than 16 bytes.
		if (remainder != 0) {
			int rowStartIndex = (fullRows << 4) + startIndex;
			appendHexDumpRowPrefix(dump, fullRows, rowStartIndex);

			// Hex dump
			int rowEndIndex = rowStartIndex + remainder;
			for (int j = rowStartIndex; j < rowEndIndex; j++) {
				dump.append(BYTE2HEX[(int) array[j]]);

			}
			dump.append(HEXPADDING[remainder]);
			dump.append(" |");

			// Ascii dump
			for (int j = rowStartIndex; j < rowEndIndex; j++) {
				dump.append(BYTE2CHAR[(int) array[j]]);

			}
			dump.append(BYTEPADDING[remainder]);
			dump.append('|');
		}

		dump.append(NEWLINE + "+--------+-------------------------------------------------+----------------+");
	}

	private static void appendHexDumpRowPrefix(StringBuilder dump, int row, int rowStartIndex) {
		if (row < HEXDUMP_ROWPREFIXES.length) {
			dump.append(HEXDUMP_ROWPREFIXES[row]);
		} else {
			dump.append(NEWLINE);
			dump.append(Long.toHexString(rowStartIndex & 0xFFFFFFFFL | 0x100000000L));
			dump.setCharAt(dump.length() - 9, '|');
			dump.append('|');
		}
	}
}

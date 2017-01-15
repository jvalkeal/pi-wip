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

import org.springframework.cloud.iot.component.ShiftRegister;

public class SevenSegmentDisplay {

	private int[] intMapping = new int[] { 0x3F, 0x06, 0x5B, 0x4F, 0x66, 0x6D, 0x7D, 0x07, 0x7F, 0x6F };
	private static int[] charMapping = new int[128];
	private ShiftRegister shiftRegister;

	static {
		charMapping[97] = 0x77;
		charMapping[98] = 0x7C;
		charMapping[99] = 0x39;
		charMapping[100] = 0x5E;
		charMapping[101] = 0x79;
		charMapping[102] = 0x71;
	}

	public SevenSegmentDisplay(ShiftRegister shiftRegister) {
		this.shiftRegister = shiftRegister;
	}

	public void setChar(char c) {
		setChar(c, false);
	}

	public void setChar(char c, boolean dot) {
		shiftRegister.shift(charMapping[(int)c]);
		shiftRegister.store();
	}

	public void setInt(int i) {
		setInt(i, false);
	}

	public void setInt(int i, boolean dot) {
		shiftRegister.shift(intMapping[i % 10]);
		shiftRegister.store();
	}

}

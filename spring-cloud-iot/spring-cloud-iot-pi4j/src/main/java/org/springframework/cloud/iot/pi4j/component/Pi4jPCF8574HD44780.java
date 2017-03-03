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
package org.springframework.cloud.iot.pi4j.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pi4jPCF8574HD44780 extends AbstractPi4jHD44780 {

	private final static Logger log = LoggerFactory.getLogger(Pi4jPCF8574HD44780.class);

	private final Pi4jPCF8574 pcf8574;
	private int d4Pin = 0;
	private int d5Pin = 1;
	private int d6Pin = 2;
	private int d7Pin = 3;
	private int rsPin = 4;
	private int rwPin = 5;
	private int ePin = 6;
	private int backlightPin = 7;

	public Pi4jPCF8574HD44780(Pi4jPCF8574 pcf8574) {
		super(true);
		this.pcf8574 = pcf8574;
	}

	@Override
	protected void write4Bit(int data, boolean registerSelect, boolean writeUpperBits) {
		if (writeUpperBits) {
			int myData = data & 0xF0;
			if (registerSelect) {
				myData |= 0x01;
			}
			myData |= 0x08;
			pcf8574.write(myData);
		} else {
			int myData = data << 4;
			if (registerSelect) {
				myData |= 0x01;
			}
			myData |= 0x08;
			pcf8574.write(myData);
		}
	}

	@Override
	protected void instructExecute(int data, boolean registerSelect, boolean writeUpperBits) {
		if (writeUpperBits) {
			int myData = data & 0xF0;
			if (registerSelect) {
				myData |= 0x01;
			}
			myData |= 0x04;
			myData |= 0x08;
			pcf8574.write(myData);
		} else {
			int myData = data << 4;
			if (registerSelect) {
				myData |= 0x01;
			}
			myData |= 0x04;
			myData |= 0x08;
			pcf8574.write(myData);
		}
		if (writeUpperBits) {
			int myData = data & 0xF0;
			if (registerSelect) {
				myData |= 0x01;
			}
			myData |= 0x08;
			pcf8574.write(myData);
		} else {
			int myData = data << 4;
			if (registerSelect) {
				myData |= 0x01;
			}
			myData |= 0x08;
			pcf8574.write(myData);
		}
	}

}

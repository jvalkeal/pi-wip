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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

/**
 * {@code PCF8574} is a Remote 8-Bit I/O Expander for I2C-Bus.
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jPCF8574 {

	private final static Logger log = LoggerFactory.getLogger(Pi4jPCF8574.class);
	private final I2CDevice i2cDevice;

	public Pi4jPCF8574(int i2cBus, int i2cAddress) throws IOException, UnsupportedBusNumberException {
		this.i2cDevice = I2CFactory.getInstance(i2cBus).getDevice(i2cAddress);
	}

	public void write(int data) {
		log.debug("Writing data {} {}", data, String.format("%8s", Integer.toBinaryString(data & 0xFF)).replace(' ', '0'));
		try {
			i2cDevice.write((byte)data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int read() {
		try {
			return i2cDevice.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

}

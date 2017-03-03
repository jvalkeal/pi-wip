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
import org.springframework.util.Assert;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

/**
 * The PCF8591 is a single-chip, single‑supply low‑power 8‑bit CMOS data
 * acquisition device with four analog inputs, one analog output and a serial
 * I2C‑bus interface.
 *
 * {@code Pi4jPCF8591} is a higher level component abstracting access to
 * supported features without needing to worry about lower level I2C access.
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jPCF8591 {

	private final static Logger log = LoggerFactory.getLogger(Pi4jPCF8591.class);
	private boolean autoIncrement = false;
	private AnalogInputMode analogInputMode = AnalogInputMode.FOUR_SINGLE_ENDED;
	private boolean analogOutput = true;
	private final I2CDevice i2cDevice;

	/**
	 * Instantiates a new pi4j PCF8591.
	 *
	 * @param i2cBus the i2c bus
	 * @param i2cAddress the i2c address
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedBusNumberException the unsupported bus number exception
	 */
	public Pi4jPCF8591(int i2cBus, int i2cAddress) throws IOException, UnsupportedBusNumberException {
		this.i2cDevice = I2CFactory.getInstance(i2cBus).getDevice(i2cAddress);
	}

	/**
	 * Read channel.
	 *
	 * @param channel the channel
	 * @return the int
	 */
	public int readChannel(Channel channel) {
		if (channel == null) {
			throw new IllegalArgumentException("Channel must be set");
		}
		int data = -1;

		byte[] writeBuffer = new byte[1];
		byte[] readBuffer = new byte[2];

		if (analogOutput) {
			// flip second bit 0x40 = 01000000
			writeBuffer[0] ^= 0x40;
		}

		if (autoIncrement) {
			// flip 6th bit 0x04 = 00000100
			writeBuffer[0] ^= 0x04;
		}

		// flip based on input mode
		writeBuffer[0] ^= getAnalogInputMode().value;

		// flip channel bit
		writeBuffer[0] ^= channel.value;

		try {
			i2cDevice.read(writeBuffer, 0, 1, readBuffer, 0, 2);
			data = readBuffer[1] & 0xFF;
		} catch (IOException e) {
			log.error("Error reading i2c bus", e);
		}

		return data;
	}

	/**
	 * Read all channels.
	 *
	 * @return the int[]
	 */
	public int[] readAllChannels() {
		int[] data = new int[4];
		byte[] writeBuffer = new byte[1];
		byte[] readBuffer = new byte[5];

		if (analogOutput) {
			// flip second bit 0x40 = 01000000
			writeBuffer[0] ^= 0x40;
		}

		if (autoIncrement) {
			// flip 6th bit 0x04 = 00000100
			writeBuffer[0] ^= 0x04;
		}

		// flip based on input mode
		writeBuffer[0] ^= getAnalogInputMode().value;

		try {
			i2cDevice.read(writeBuffer, 0, 1, readBuffer, 0, 5);
			data[0] = readBuffer[1] & 0xFF;
			data[1] = readBuffer[2] & 0xFF;
			data[2] = readBuffer[3] & 0xFF;
			data[3] = readBuffer[4] & 0xFF;
		} catch (IOException e) {
			log.error("Error reading i2c bus", e);
			data[0] = data[1] = data[2] = data[3] = -1;
		}

		return data;
	}

	/**
	 * Checks if is auto increment.
	 *
	 * @return true, if is auto increment
	 */
	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	/**
	 * Sets the auto increment.
	 *
	 * @param autoIncrement the new auto increment
	 */
	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	/**
	 * Gets the analog input mode.
	 *
	 * @return the analog input mode
	 */
	public AnalogInputMode getAnalogInputMode() {
		return analogInputMode;
	}

	/**
	 * Sets the analog input mode.
	 *
	 * @param analogInputMode the new analog input mode
	 */
	public void setAnalogInputMode(AnalogInputMode analogInputMode) {
		Assert.notNull(analogInputMode, "'analogInputMode' must be set");
		this.analogInputMode = analogInputMode;
	}

	/**
	 * Checks if is analog output.
	 *
	 * @return true, if is analog output
	 */
	public boolean isAnalogOutput() {
		return analogOutput;
	}

	/**
	 * Sets the analog output.
	 *
	 * @param analogOutput the new analog output
	 */
	public void setAnalogOutput(boolean analogOutput) {
		this.analogOutput = analogOutput;
	}

	/**
	 * Enumeration for possible analog input modes.
	 */
	public enum AnalogInputMode {

		/**
		 * <pre>
		 * AIN0 - channel 0
		 * AIN1 - channel 1
		 * AIN2 - channel 2
		 * AIN3 - channel 3
		 * </pre>
		 *
		 * Value for flipping bit 0x00 = 00000000
		 */
		FOUR_SINGLE_ENDED(0x00),

		/**
		 * <pre>
		 * AIN0 ---|+|
		 *         | |-- channel 0
		 *       +-|-|
		 *       |
		 * AIN1 ---|+|
		 *       | | |-- channel 1
		 *       +-|-|
		 *       |
		 * AIN2 ---|+|
		 *       | | |-- channel 2
		 * AIN3 -+-|-|
		 * </pre>
		 *
		 * Value for flipping bit 0x10 = 00010000
		 */
		THREE_DIFFERENTIAL(0x10),

		/**
		 * <pre>
		 * AIN0 channel 0
		 * AIN1 channel 1
		 *
		 * AIN2 ---|+|
		 *         | |-- channel 2
		 * AIN3 ---|-|
		 * </pre>
		 *
		 * Value for flipping bit 0x20 = 00100000
		 */
		SINGLE_ENDED_DIFFERENTIAL_MIXED(0x20),

		/**
		 * <pre>
		 * AIN0 ---|+|
		 *         | |-- channel 0
		 * AIN1 ---|-|
		 *
		 * AIN2 ---|+|
		 *         | |-- channel 1
		 * AIN3 ---|-|
		 * </pre>
		 *
		 * Value for flipping bits 0x30 = 00110000
		 */
		TWO_DIFFERENTIAL(0x30);

		private int value;

		private AnalogInputMode(int value) {
			this.value = value;
		}
	}

	/**
	 * Enumeration for channel read/write requests.
	 */
	public enum Channel {
		CHANNEL_0(0x00),
		CHANNEL_1(0x01),
		CHANNEL_2(0x02),
		CHANNEL_3(0x03);

		private int value;

		private Channel(int value) {
			this.value = value;
		}
	}
}

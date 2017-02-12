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
package org.springframework.cloud.iot.pi4j;

import java.io.IOException;

import org.springframework.cloud.iot.IotSystemException;
import org.springframework.cloud.iot.component.sensor.PotentiometerSensor;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

/**
 * {@code Pi4jPCF8591Potentiometer} is a {@link PotentiometerSensor} expecting
 * to get connected through a {@code PCF8591}.
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jPCF8591Potentiometer extends LifecycleObjectSupport implements PotentiometerSensor {

	private final int i2cBus;
	private final int i2cAddr;
	private final int min;
	private final int max;
	private final I2CDevice dev;

	/**
	 * Instantiates a new Pi4jPCF8591Potentiometer instance.
	 *
	 * @param i2cBus the i2c bus
	 * @param i2cAddr the i2c addr
	 * @param min the min
	 * @param max the max
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedBusNumberException the unsupported bus number exception
	 */
	public Pi4jPCF8591Potentiometer(int i2cBus, int i2cAddr, int min, int max)
			throws IOException, UnsupportedBusNumberException {
		this.i2cBus = i2cBus;
		this.i2cAddr = i2cAddr;
		this.min = min;
		this.max = max;
		this.dev = I2CFactory.getInstance(i2cBus).getDevice(i2cAddr);
	}

	@Override
	public String getName() {
		return "potentiometer";
	}

	@Override
	public int getMin() {
		return min;
	}

	@Override
	public int getMax() {
		return max;
	}

	@Override
	public int getValue() {
		try {
			return dev.read();
		} catch (IOException e) {
			throw new IotSystemException("Error reading from I2C bus=[" + i2cBus + "] addr=[" + i2cAddr + "]", e);
		}
	}
}

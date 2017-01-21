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
package org.springframework.cloud.iot.pi4j.support;

import java.io.IOException;

import org.springframework.cloud.iot.support.IotUtils;

import com.pi4j.component.temperature.TemperatureSensorBase;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import com.pi4j.temperature.TemperatureScale;

public class Termistor extends TemperatureSensorBase {

	private final I2CDevice dev;
	private final double voltageSupply;
	private final int dacBits;
	private final int resistance;
	private final double beta;
	private final double referenceTemp;

	/**
	 * Instantiates a new termistor.
	 *
	 * @param i2cBus the i2c bus
	 * @param i2cAddr the i2c addr
	 * @param voltageSupply the system voltage supply
	 * @param dacBits the ADC bits
	 * @param resistance the resistance
	 * @param beta the beta parameter value
	 * @param referenceTemp the refeference temp
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedBusNumberException the unsupported bus number exception
	 */
	public Termistor(int i2cBus, int i2cAddr, double voltageSupply, int dacBits, int resistance, double beta,
			double referenceTemp)
			throws IOException, UnsupportedBusNumberException {
		this.dev = I2CFactory.getInstance(i2cBus).getDevice(i2cAddr);
		this.voltageSupply = voltageSupply;
		this.dacBits = dacBits;
		this.resistance = resistance;
		this.beta = beta;
		this.referenceTemp = referenceTemp;
	}



	@Override
	public String toString() {
		return "Termistor [voltageSupply=" + voltageSupply + ", dacBits=" + dacBits + ", resistance=" + resistance + ", beta=" + beta
				+ ", referenceTemp=" + referenceTemp + "]";
	}



	@Override
	public double getTemperature() {
		byte[] tempBuffer = new byte[1];

		try {
			dev.read(tempBuffer, 0, 1);
			return IotUtils.getSteinhartHartTemperature((float)tempBuffer[0], voltageSupply, dacBits, resistance, beta, referenceTemp);
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	@Override
	public TemperatureScale getScale() {
		return TemperatureScale.KELVIN;
	}
}

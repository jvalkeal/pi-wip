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

import org.springframework.cloud.iot.component.sensor.Potentiometer;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class Pi4jPCF8591Potentiometer extends LifecycleObjectSupport implements Potentiometer {

	private final I2CDevice dev;
	private int min;
	private int max;

	public Pi4jPCF8591Potentiometer(int i2cBus, int i2cAddr, int min, int max) throws IOException, UnsupportedBusNumberException {
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
			throw new RuntimeException();
		}
	}
}

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

	public Termistor(int i2cBus, int i2cAddr) throws IOException, UnsupportedBusNumberException {
		this.dev = I2CFactory.getInstance(i2cBus).getDevice(i2cAddr);
	}

	@Override
	public double getTemperature() {
		byte[] tempBuffer = new byte[1];

		try {
			dev.read(tempBuffer, 0, 1);
			return IotUtils.getSteinhartHartTemperature((float)tempBuffer[0], 5, 8, 1000, 3950, 25);
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	@Override
	public TemperatureScale getScale() {
		return TemperatureScale.CELSIUS;
	}
}

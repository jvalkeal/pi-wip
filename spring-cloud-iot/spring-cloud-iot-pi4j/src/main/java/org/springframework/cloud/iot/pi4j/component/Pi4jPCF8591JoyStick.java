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
import java.time.Duration;
import java.util.concurrent.Callable;

import org.springframework.cloud.iot.component.joystick.Joystick;
import org.springframework.cloud.iot.event.JoystickEvent;
import org.springframework.cloud.iot.listener.CompositeJoystickListener;
import org.springframework.cloud.iot.listener.JoystickListener;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;
import org.springframework.cloud.iot.support.ReactiveSensorValue;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import reactor.core.Disposable;

/**
 * {@code Pi4jPCF8591JoyStick} is a {@link Joystick} implementation which is
 * expected to be connected into I2C with {@code Pi4jPCF8591} ADC converter.
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jPCF8591JoyStick extends LifecycleObjectSupport implements Joystick {

	private final Pi4jPCF8591 adc;
	private final ReactiveSensorValue<int[]> sensorValue;
	private final CompositeJoystickListener compositeListener = new CompositeJoystickListener();
	private Disposable disposable;

	/**
	 * Instantiates a new pi4j PCF8591 joystick.
	 *
	 * @param i2cBus the i2c bus
	 * @param i2cAddress the i2c address
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedBusNumberException the unsupported bus number exception
	 */
	public Pi4jPCF8591JoyStick(int i2cBus, int i2cAddress) throws IOException, UnsupportedBusNumberException {
		this(i2cBus, i2cAddress, Duration.ofMillis(100));
	}

	/**
	 * Instantiates a new pi4j PCF8591 joystick.
	 *
	 * @param i2cBus the i2c bus
	 * @param i2cAddress the i2c address
	 * @param duration the polling interval
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedBusNumberException the unsupported bus number exception
	 */
	public Pi4jPCF8591JoyStick(int i2cBus, int i2cAddress, Duration duration) throws IOException, UnsupportedBusNumberException {
		this.adc = new Pi4jPCF8591(i2cBus, i2cAddress);
		this.adc.setAutoIncrement(true);
		this.sensorValue = new ReactiveSensorValue<>(new Callable<int[]>() {

			@Override
			public int[] call() throws Exception {
				return adc.readAllChannels();
			}
		}, duration);
		setAutoStartup(true);
	}

	@Override
	protected void onInit() throws Exception {
		this.sensorValue.afterPropertiesSet();
	}

	@Override
	protected void doStart() {
		if (disposable != null) {
			return;
		}
		disposable = sensorValue.asFlux().subscribe(values -> {
			int deltaY = values[0] - 128;
			int deltaX = values[1] - 128;
			int button = values[2];
			// check some level changes as we assume small
			// voltage changes are just noise
			if (Math.abs(deltaY) > 10 || Math.abs(deltaX) > 10 || button < 10) {
				double x = deltaX < 0 ? deltaX / 128.0 : deltaX / 127.0;
				double y = deltaY < 0 ? deltaY / 128.0 : deltaY / 127.0;
				compositeListener.onJoystickEvent(new JoystickEvent(this, x, y, values[2] < 10));
			}
		});
	}

	@Override
	protected void doStop() {
		super.doStop();
		if (disposable != null) {
			disposable.dispose();
		}
		disposable = null;
	}

	@Override
	public void addJoystickListener(JoystickListener listener) {
		compositeListener.register(listener);
	}

	@Override
	public void removeJoystickListener(JoystickListener listener) {
		compositeListener.unregister(listener);
	}
}

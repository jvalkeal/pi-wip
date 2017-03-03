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
package org.springframework.cloud.iot.properties;

/**
 * Properties for {@code Joystick} devices.
 *
 * @author Janne Valkealahti
 */
public class JoystickProperties extends ComponentProperties {

	/** I2C configuration */
	private I2CProperties i2c;

	/**
	 * Gets the i2c.
	 *
	 * @return the i2c
	 */
	public I2CProperties getI2c() {
		return i2c;
	}

	/**
	 * Sets the i2c.
	 *
	 * @param i2c the new i2c
	 */
	public void setI2c(I2CProperties i2c) {
		this.i2c = i2c;
	}

}

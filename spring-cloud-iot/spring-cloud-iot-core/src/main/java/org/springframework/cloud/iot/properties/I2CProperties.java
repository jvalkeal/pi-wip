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
 * Properties for configuring I2C bus.
 *
 * @author Janne Valkealahti
 */
public class I2CProperties {

	/** The I2C bus number */
	private Integer bus;

	/** The I2C bus address */
	private Integer address;

	/**
	 * Gets the bus number.
	 *
	 * @return the bus
	 */
	public Integer getBus() {
		return bus;
	}

	/**
	 * Sets the bus number.
	 *
	 * @param bus the new bus
	 */
	public void setBus(Integer bus) {
		this.bus = bus;
	}

	/**
	 * Gets the bus address.
	 *
	 * @return the bus address
	 */
	public Integer getAddress() {
		return address;
	}

	/**
	 * Sets the bus address.
	 *
	 * @param address the bus address
	 */
	public void setAddress(Integer address) {
		this.address = address;
	}
}

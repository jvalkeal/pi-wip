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
package org.springframework.cloud.iot.boot;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for "spring.cloud.iot".
 *
 * @author Janne Valkealahti
 *
 */
@ConfigurationProperties(prefix = IotConfigurationProperties.NAMESPACE)
public class IotConfigurationProperties {

	public static final String NAMESPACE = "spring.cloud.iot";

	private I2C i2c;

	public I2C getI2C() {
		return i2c;
	}

	public void setI2C(I2C i2c) {
		this.i2c = i2c;
	}

	private Device device;

	public void setDevice(Device device) {
		this.device = device;
	}

	public Device getDevice() {
		return device;
	}

	public static class Device {

		private Lcd lcd;

		public Lcd getLcd() {
			return lcd;
		}

		public void setLcd(Lcd lcd) {
			this.lcd = lcd;
		}

	}

	public static class Lcd {
		private int rows = 2;
		private int colums = 16;
		private boolean clearTextOnExit = true;
		public int getRows() {
			return rows;
		}
		public void setRows(int rows) {
			this.rows = rows;
		}
		public int getColums() {
			return colums;
		}
		public void setColums(int colums) {
			this.colums = colums;
		}
		public boolean isClearTextOnExit() {
			return clearTextOnExit;
		}
		public void setClearTextOnExit(boolean clearTextOnExit) {
			this.clearTextOnExit = clearTextOnExit;
		}
	}

	public static class I2C {

		private Map<Integer, Addresses> addresses;

		public Map<Integer, Addresses> getAddresses() {
			return addresses;
		}

		public void setAddresses(Map<Integer, Addresses> addresses) {
			this.addresses = addresses;
		}
	}

	public static class Addresses {
		private String type;
		private int bus;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		public int getBus() {
			return bus;
		}

		public void setBus(int bus) {
			this.bus = bus;
		}
	}

	public enum NumberingScheme {
		BROADCOM, WIRINGPI;
	}

}

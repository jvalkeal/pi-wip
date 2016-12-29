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
@ConfigurationProperties(prefix = "spring.cloud.iot")
public class IotConfigurationProperties {

//	private NumberingScheme numberingScheme;
	private Map<String, Pins> pins;
	private I2C i2c;

//	public NumberingScheme getNumberingScheme() {
//		return numberingScheme;
//	}
//
//	public void setNumberingScheme(NumberingScheme numberingScheme) {
//		this.numberingScheme = numberingScheme;
//	}

	public Map<String, Pins> getPins() {
		return pins;
	}

	public void setPins(Map<String, Pins> pins) {
		this.pins = pins;
	}

	public I2C getI2C() {
		return i2c;
	}

	public void setI2C(I2C i2c) {
		this.i2c = i2c;
	}

	public static class Pins {

		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return "Pins [type=" + type + "]";
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

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public enum NumberingScheme {
		BROADCOM, WIRINGPI;
	}

}

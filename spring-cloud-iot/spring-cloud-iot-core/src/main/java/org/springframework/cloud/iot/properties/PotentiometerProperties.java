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

public class PotentiometerProperties extends ComponentProperties {

	private I2CType i2c;
	private Integer min;
	private Integer max;

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public I2CType getI2c() {
		return i2c;
	}

	public void setI2c(I2CType i2c) {
		this.i2c = i2c;
	}

	public static class I2CType {

		private Integer bus;
		private Integer address;

		public Integer getBus() {
			return bus;
		}

		public void setBus(Integer bus) {
			this.bus = bus;
		}

		public Integer getAddress() {
			return address;
		}

		public void setAddress(Integer address) {
			this.address = address;
		}
	}
}

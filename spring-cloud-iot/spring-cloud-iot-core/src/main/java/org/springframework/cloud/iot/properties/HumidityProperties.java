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

public class HumidityProperties {

	private GpioType gpio;

	public GpioType getGpio() {
		return gpio;
	}

	public void setGpio(GpioType gpio) {
		this.gpio = gpio;
	}

	public static class GpioType {

		private Integer pin;

		public Integer getPin() {
			return pin;
		}

		public void setPin(Integer pin) {
			this.pin = pin;
		}
	}
}

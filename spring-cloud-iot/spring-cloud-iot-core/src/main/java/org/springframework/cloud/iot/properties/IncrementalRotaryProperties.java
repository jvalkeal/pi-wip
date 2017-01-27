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

public class IncrementalRotaryProperties extends ComponentProperties {

	private Integer incrementSteps;
	private GpioType gpio;

	public Integer getIncrementSteps() {
		return incrementSteps;
	}

	public void setIncrementSteps(Integer incrementSteps) {
		this.incrementSteps = incrementSteps;
	}

	public GpioType getGpio() {
		return gpio;
	}

	public void setGpio(GpioType gpio) {
		this.gpio = gpio;
	}

	public static class GpioType {

		private Integer leftPin;
		private Integer rightPin;
		private Integer clickPin;

		public Integer getLeftPin() {
			return leftPin;
		}

		public void setLeftPin(Integer leftPin) {
			this.leftPin = leftPin;
		}

		public Integer getRightPin() {
			return rightPin;
		}

		public void setRightPin(Integer rightPin) {
			this.rightPin = rightPin;
		}

		public Integer getClickPin() {
			return clickPin;
		}

		public void setClickPin(Integer clickPin) {
			this.clickPin = clickPin;
		}
	}
}

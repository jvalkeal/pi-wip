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
import org.springframework.cloud.iot.boot.properties.ButtonProperties;
import org.springframework.cloud.iot.boot.properties.DimmedLedProperties;
import org.springframework.cloud.iot.boot.properties.IncrementalRotaryProperties;
import org.springframework.cloud.iot.boot.properties.RelayProperties;
import org.springframework.cloud.iot.boot.properties.ShiftRegisterProperties;

/**
 * Properties for "spring.cloud.iot".
 *
 * @author Janne Valkealahti
 *
 */
@ConfigurationProperties(prefix = IotConfigurationProperties.NAMESPACE)
public class IotConfigurationProperties {

	public static final String NAMESPACE = "spring.cloud.iot";

	private Map<String, ComponentType> components;

	public Map<String, ComponentType> getComponents() {
		return components;
	}

	public void setComponents(Map<String, ComponentType> components) {
		this.components = components;
	}

	public static class ComponentType {

		private IncrementalRotaryProperties incrementalRotary;
		private ButtonProperties button;
		private DimmedLedProperties dimmedLed;
		private RelayProperties relay;
		private ShiftRegisterProperties shiftRegister;

		public IncrementalRotaryProperties getIncrementalRotary() {
			return incrementalRotary;
		}

		public void setIncrementalRotary(IncrementalRotaryProperties incrementalRotary) {
			this.incrementalRotary = incrementalRotary;
		}

		public ButtonProperties getButton() {
			return button;
		}

		public void setButton(ButtonProperties button) {
			this.button = button;
		}

		public DimmedLedProperties getDimmedLed() {
			return dimmedLed;
		}

		public void setDimmedLed(DimmedLedProperties dimmedLed) {
			this.dimmedLed = dimmedLed;
		}

		public RelayProperties getRelay() {
			return relay;
		}

		public void setRelay(RelayProperties relay) {
			this.relay = relay;
		}

		public ShiftRegisterProperties getShiftRegister() {
			return shiftRegister;
		}

		public void setShiftRegister(ShiftRegisterProperties shiftRegister) {
			this.shiftRegister = shiftRegister;
		}
	}

	public enum NumberingScheme {
		BROADCOM, WIRINGPI;
	}

}

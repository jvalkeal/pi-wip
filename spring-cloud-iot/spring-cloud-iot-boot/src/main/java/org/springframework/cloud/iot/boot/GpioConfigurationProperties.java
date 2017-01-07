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

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for "spring.cloud.iot.gpio".
 *
 * @author Janne Valkealahti
 *
 */
@ConfigurationProperties(prefix = GpioConfigurationProperties.NAMESPACE)
public class GpioConfigurationProperties {

	public static final String NAMESPACE = "spring.cloud.iot.gpio";

	private Map<Integer, PinProperties> pins = new HashMap<>();

	public Map<Integer, PinProperties> getPins() {
		return pins;
	}

	public void setPins(Map<Integer, PinProperties> pins) {
		this.pins = pins;
	}

	/**
	 * Configuration for gpio pin.
	 */
	public static class PinProperties {

		private PinComponentType component;

		public PinComponentType getComponent() {
			return component;
		}

		public void setComponent(PinComponentType component) {
			this.component = component;
		}
	}

	public static enum PinComponentType {
		DIMMEDLED,RELAY,TERMISTOR;
	}
}

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
package org.springframework.cloud.iot.pi4j;

import org.springframework.cloud.iot.component.Relay;

import com.pi4j.component.relay.impl.GpioRelayComponent;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class Pi4jGpioRelayComponent implements Relay {

	private final GpioPinDigitalOutput output;
	private final GpioRelayComponent component;

	public Pi4jGpioRelayComponent(GpioPinDigitalOutput output) {
		this.output = output;
		this.component = new GpioRelayComponent(output);
	}

	@Override
	public void setOpen() {
		component.open();
	}

	@Override
	public void setClosed() {
		component.close();
	}
}

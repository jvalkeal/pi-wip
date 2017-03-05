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

import org.springframework.cloud.iot.component.Led;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

/**
 * {@code Pi4jLed} is a {@link Led} implementation.
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jLed extends LifecycleObjectSupport implements Led {

	private final GpioPinDigitalOutput pin;
	private Boolean illuminateOnStart;
	private Boolean illuminateOnExit;

	public Pi4jLed(GpioPinDigitalOutput pin) {
		this(pin, null, null);
	}

	public Pi4jLed(GpioPinDigitalOutput pin, Boolean illuminateOnStart, Boolean illuminateOnExit) {
		this.pin = pin;
		this.illuminateOnStart = illuminateOnStart;
		this.illuminateOnExit = illuminateOnExit;
		setAutoStartup(true);
	}

	@Override
	protected void doStart() {
		if (illuminateOnStart != null) {
			pin.setState(!illuminateOnStart);
		}
	}

	@Override
	protected void doStop() {
		if (illuminateOnExit != null) {
			pin.setState(!illuminateOnExit);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		pin.setState(enabled);
	}
}

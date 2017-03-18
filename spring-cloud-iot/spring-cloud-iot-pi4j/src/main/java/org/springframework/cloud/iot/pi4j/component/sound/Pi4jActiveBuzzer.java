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
package org.springframework.cloud.iot.pi4j.component.sound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.component.sound.ActiveBuzzer;
import org.springframework.cloud.iot.support.IotObjectSupport;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

public class Pi4jActiveBuzzer extends IotObjectSupport implements ActiveBuzzer {

	private static final Logger log = LoggerFactory.getLogger(Pi4jActiveBuzzer.class);
	private final GpioPinDigitalOutput output;

	public Pi4jActiveBuzzer(GpioPinDigitalOutput output) {
		this.output = output;
	}

	@Override
	protected void onInit() throws Exception {
		log.info("init, set pin HIGH");
		output.setState(PinState.HIGH);
	}

	@Override
	public void setEnabled(boolean enabled) {
		output.setState(!enabled);
	}

	@Override
	protected void doDestroy() {
		log.info("destroy, set pin HIGH");
		output.setState(PinState.HIGH);
	}
}

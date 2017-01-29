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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.component.Button;
import org.springframework.cloud.iot.listener.ButtonListener;
import org.springframework.cloud.iot.listener.CompositeButtonListener;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;

import com.pi4j.component.button.ButtonState;
import com.pi4j.component.button.ButtonStateChangeEvent;
import com.pi4j.component.button.ButtonStateChangeListener;
import com.pi4j.component.button.impl.GpioButtonComponent;
import com.pi4j.io.gpio.GpioPinDigitalInput;

public class Pi4jButton extends LifecycleObjectSupport implements Button {

	private final static Logger log = LoggerFactory.getLogger(Pi4jButton.class);
	private GpioButtonComponent button;
	private CompositeButtonListener buttonListener;

	public Pi4jButton(GpioPinDigitalInput input) {
		this.button = new GpioButtonComponent(input);
	}



	@Override
	protected void onInit() throws Exception {
		log.info("Adding ButtonStateChangeListener");
		button.addListener(new ButtonStateChangeListener() {

			@Override
			public void onStateChange(ButtonStateChangeEvent event) {
				log.info("ButtonStateChangeEvent {}", event.getNewState());
				if (buttonListener != null) {
					if (event.getNewState() == ButtonState.PRESSED) {
						buttonListener.onPressed();
					} else if (event.getNewState() == ButtonState.RELEASED) {
						buttonListener.onReleased();
					}
				}
			}
		});
	}



	@Override
	public void addButtonListener(ButtonListener listener) {
		synchronized (this) {
			if (buttonListener == null) {
				buttonListener = new CompositeButtonListener();
			}
			buttonListener.register(listener);
		}
	}



	@Override
	public void removeButtonListener(ButtonListener listener) {
		synchronized (this) {
			if (buttonListener != null) {
				buttonListener.unregister(listener);
			}
		}
	}

}

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

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.component.Button;
import org.springframework.cloud.iot.event.ButtonEvent;
import org.springframework.cloud.iot.event.IotEventPublisher;
import org.springframework.cloud.iot.listener.ButtonListener;
import org.springframework.cloud.iot.listener.CompositeButtonListener;
import org.springframework.cloud.iot.support.IotObjectSupport;

import com.pi4j.component.button.ButtonState;
import com.pi4j.component.button.ButtonStateChangeEvent;
import com.pi4j.component.button.ButtonStateChangeListener;
import com.pi4j.component.button.impl.GpioButtonComponent;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;

/**
 * {@link Button} implementation based on {@code Pi4J}.
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jButton extends IotObjectSupport implements Button {

	private final static Logger log = LoggerFactory.getLogger(Pi4jButton.class);
	private GpioButtonComponent button;
	private CompositeButtonListener buttonListener;
	private final GpioPinDigitalInput input;

	/**
	 * Instantiates a new Pi4jButton.
	 *
	 * @param input the input
	 */
	public Pi4jButton(GpioPinDigitalInput input) {
		this(input, null);
	}

	public Pi4jButton(GpioPinDigitalInput input, Collection<String> tags) {
		this.input = input;
		this.button = new GpioButtonComponent(input);
		setAutoStartup(true);
		setTags(tags);
	}

	@Override
	protected void onInit() throws Exception {
		log.debug("Adding ButtonStateChangeListener {}", this);
		button.addListener(new ButtonStateChangeListener() {

			@Override
			public void onStateChange(ButtonStateChangeEvent event) {
				log.debug("Pi4j ButtonStateChangeEvent {}", event.getNewState());
				// pi4j expects pull resistance to be DOWN and connected to VCC,
				// thus switch event other way around if user configured
				// button to be connected to GND.
				if (event.getNewState() == ButtonState.PRESSED) {
					if (input.getPullResistance() == PinPullResistance.PULL_DOWN) {
						if (buttonListener != null) {
							buttonListener.onPressed();
						}
						notifyButtonPressed(true);
					} else {
						if (buttonListener != null) {
							buttonListener.onReleased();
						}
						notifyButtonPressed(false);
					}
				} else if (event.getNewState() == ButtonState.RELEASED) {
					if (input.getPullResistance() == PinPullResistance.PULL_DOWN) {
						if (buttonListener != null) {
							buttonListener.onReleased();
						}
						notifyButtonPressed(false);
					} else {
						if (buttonListener != null) {
							buttonListener.onPressed();
						}
						notifyButtonPressed(true);
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

	private void notifyButtonPressed(boolean pressed) {
		if (isContextEventsEnabled()) {
			IotEventPublisher publisher = getIotEventPublisher();
			if (publisher != null) {
				publisher.publishIotEvent(new ButtonEvent(this, pressed, getTags(), getProperties()));
			}
		}
	}
}

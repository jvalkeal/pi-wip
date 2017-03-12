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
package org.springframework.cloud.iot.event;

import java.util.Collection;

import org.springframework.cloud.iot.component.Button;

/**
 * {@link IotEvent} class for {@link Button} based events.
 *
 * @author Janne Valkealahti
 *
 */
public class ButtonEvent extends IotEvent {

	private static final long serialVersionUID = 206042361622621872L;
	private final boolean pressed;
	public static final String EVENT_ID = "BUTTON";

	/**
	 * Instantiates a new button event.
	 *
	 * @param source the source
	 * @param pressed the pressed
	 */
	public ButtonEvent(Object source, boolean pressed) {
		super(source);
		this.pressed = pressed;
	}

	public ButtonEvent(Object source, boolean pressed, Collection<String> tags) {
		super(source);
		this.pressed = pressed;
		if (tags != null) {
			this.tags.addAll(tags);
		}
	}

	@Override
	public String getEventId() {
		return EVENT_ID;
	}

	/**
	 * Checks if is pressed.
	 *
	 * @return true, if is pressed
	 */
	public boolean isPressed() {
		return pressed;
	}

	@Override
	public String toString() {
		return "ButtonEvent [pressed=" + pressed + ", toString()=" + super.toString() + "]";
	}
}

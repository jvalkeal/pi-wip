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
import java.util.Map;

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
	public static final String EVENT_ID_PRESSED = "BUTTON_PRESSED";
	public static final String EVENT_ID_RELEASED = "BUTTON_RELEASED";

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

	/**
	 * Instantiates a new button event.
	 *
	 * @param source the source
	 * @param pressed the pressed
	 * @param tags the tags
	 * @param properties the properties
	 */
	public ButtonEvent(Object source, boolean pressed, Collection<String> tags, Map<String, Object> properties) {
		super(source);
		this.pressed = pressed;
		if (tags != null) {
			this.tags.addAll(tags);
		}
		if (properties != null) {
			this.properties.putAll(properties);
		}
	}

	@Override
	public String getEventId() {
		return pressed ? EVENT_ID_PRESSED : EVENT_ID_RELEASED;
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

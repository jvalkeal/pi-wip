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

import org.springframework.cloud.iot.component.joystick.Joystick;

/**
 * {@code JoystickEvent} is an {@code IotEvent} send by {@link Joystick}.
 *
 * @author Janne Valkealahti
 *
 */
public class JoystickEvent extends IotEvent {

	private static final long serialVersionUID = 1537242195060301535L;
	private final double x;
	private final double y;
	private final boolean pressed;

	/**
	 * Instantiates a new joystick event.
	 *
	 * @param source the source
	 * @param x the x
	 * @param y the y
	 * @param pressed the pressed
	 */
	public JoystickEvent(Object source, double x, double y, boolean pressed) {
		super(source);
		this.x = x;
		this.y = y;
		this.pressed = pressed;
	}

	/**
	 * Gets the x axis position between -1.0..1.0.
	 *
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * Gets the y axis position between -1.0..1.0.
	 *
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * Checks if is pressed.
	 *
	 * @return true, if is pressed
	 */
	public boolean isPressed() {
		return pressed;
	}

	/**
	 * Gets the degree position as value between 0..359.
	 *
	 * @return the degree
	 */
	public long getDegree() {
		double rad = Math.atan2(y, x);
		double d = (rad * (180 / Math.PI));
		long degree = Math.round(d);
		if (y < 0) {
			// atan2 gives values 0..180 -180..0
			// so need to flip
			degree = 360 + degree;
		}
		return degree;
	}

	@Override
	public String toString() {
		return "JoystickEvent [x=" + x + ", y=" + y + ", pressed=" + pressed + ", getDegree()=" + getDegree() + "]";
	}
}

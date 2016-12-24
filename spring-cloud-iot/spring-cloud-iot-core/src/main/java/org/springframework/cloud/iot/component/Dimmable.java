/*
 * Copyright 2016 the original author or authors.
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
package org.springframework.cloud.iot.component;

/**
 * {@code Dimmable} represents a device which uses
 * Pulse Width Modulation (PWM) to set an active duty
 * cycle. Modulation is represented by a percentage.
 *
 * @author Janne Valkealahti
 *
 */
public interface Dimmable {

	/**
	 * Sets the pwm percentage. Value is given as integer
	 * ranging from 0 to 100 inclusive. Values below 0 and above
	 * 100 are treated as 0 and 100 respectively.
	 *
	 * @param percent the pwm percentage
	 */
	void setPercentage(int percent);
}

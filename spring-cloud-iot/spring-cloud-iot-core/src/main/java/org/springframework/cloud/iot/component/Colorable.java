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
package org.springframework.cloud.iot.component;

/**
 * {@code Colorable} represents a device which knows
 * how to set a color using a simple rgb value.
 *
 * @author Janne Valkealahti
 *
 */
public interface Colorable {

	/**
	 * Sets the rgb color.
	 *
	 * @param color the color as rgb value
	 */
	void setColor(int color);
}

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
package org.springframework.cloud.iot.listener;

import java.util.Iterator;

import org.springframework.cloud.iot.event.JoystickEvent;
import org.springframework.cloud.iot.support.AbstractCompositeItems;

public class CompositeJoystickListener extends AbstractCompositeItems<JoystickListener> implements JoystickListener {

	@Override
	public void onJoystickEvent(JoystickEvent event) {
		for (Iterator<JoystickListener> iterator = getItems().reverse(); iterator.hasNext();) {
			JoystickListener listener = iterator.next();
			listener.onJoystickEvent(event);
		}
	}
}

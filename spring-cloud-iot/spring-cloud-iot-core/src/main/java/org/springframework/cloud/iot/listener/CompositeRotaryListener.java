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

import org.springframework.cloud.iot.support.AbstractCompositeItems;

public class CompositeRotaryListener extends AbstractCompositeItems<RotaryListener> implements RotaryListener {

	@Override
	public void onRotate() {
		for (Iterator<RotaryListener> iterator = getItems().reverse(); iterator.hasNext();) {
			RotaryListener listener = iterator.next();
			listener.onRotate();
		}
	}
}

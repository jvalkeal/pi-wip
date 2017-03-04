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
package org.springframework.cloud.iot.test.fake;

import org.springframework.cloud.iot.component.Button;
import org.springframework.cloud.iot.event.ButtonEvent;
import org.springframework.cloud.iot.event.IotEventPublisher;
import org.springframework.cloud.iot.listener.ButtonListener;
import org.springframework.cloud.iot.listener.CompositeButtonListener;
import org.springframework.cloud.iot.support.IotObjectSupport;
import org.springframework.scheduling.annotation.Scheduled;

public class FakeButton extends IotObjectSupport implements Button {

	private CompositeButtonListener buttonListener;
	private boolean pressed = false;

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

	@Scheduled(fixedRate = 2000)
	public void sendNextToggle() {
		boolean state = (pressed = !pressed);
		if (buttonListener != null) {
			if (state) {
				buttonListener.onPressed();
			} else {
				buttonListener.onReleased();
			}
		}
		notifyButtonPressed();
	}

	private void notifyButtonPressed() {
		if (isContextEventsEnabled()) {
			IotEventPublisher publisher = getIotEventPublisher();
			if (publisher != null) {
				publisher.publishIotEvent(new ButtonEvent(this));
			}
		}
	}
}

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
package org.springframework.cloud.iot.xbee;

import org.springframework.cloud.iot.xbee.listener.XBeeReceiverListener;
import org.springframework.messaging.Message;

/**
 * Higher level abstraction over raw {@code XBee} devices allowing to easier
 * communication over radio network. This interface provides receiving features
 * using Spring messaging system where base message is {@link Message}.
 *
 * @author Janne Valkealahti
 * @see XBeeSender
 *
 */
public interface XBeeReceiver {

	/**
	 * Register the {@link XBeeReceiverListener}.
	 *
	 * @param listener the listener
	 */
	void addXBeeReceiverListener(XBeeReceiverListener listener);

	/**
	 * Unregister the {@link XBeeReceiverListener}.
	 *
	 * @param listener the listener
	 */
	void removeXBeeReceiverListener(XBeeReceiverListener listener);
}

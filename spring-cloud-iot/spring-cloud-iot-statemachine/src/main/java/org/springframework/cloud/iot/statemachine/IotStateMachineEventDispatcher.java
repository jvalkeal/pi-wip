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
package org.springframework.cloud.iot.statemachine;

import org.springframework.cloud.iot.event.IotEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.statemachine.StateMachine;

/**
 * {@link ApplicationListener} which dispatch {@link IotEvent}s into configured
 * {@link StateMachine}.
 *
 * @author Janne Valkealahti
 *
 */
public class IotStateMachineEventDispatcher implements ApplicationListener<IotEvent> {

	private final StateMachine<String, String> stateMachine;

	/**
	 * Instantiates a new iot state machine event dispatcher.
	 *
	 * @param stateMachine the state machine
	 */
	public IotStateMachineEventDispatcher(StateMachine<String, String> stateMachine) {
		this.stateMachine = stateMachine;
	}

	@Override
	public void onApplicationEvent(IotEvent event) {
		stateMachine.sendEvent(event.getEventId());
	}
}

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
package demo.gamebuttons;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.statemachine.IotStateMachineConstants;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.OnStateEntry;
import org.springframework.statemachine.annotation.WithStateMachine;

/**
 * {@code MemoryGame} runs a game logic where buttons/leds will illuminate with
 * static order and player is required to press buttons on same order. With
 * every cycle one more led is illuminated and user needs to remember order.
 * Game logic will fail if wrong button in pressed. User needs to wait for
 *
 * @author Janne Valkealahti
 *
 */
@WithStateMachine(id = IotStateMachineConstants.ID_STATEMACHINE)
public class MemoryGame extends AbstractGame {

	private static final Logger log = LoggerFactory.getLogger(MemoryGame.class);
	private int[] queue;

	/**
	 * Instantiates a new memory game.
	 *
	 * @param applicationProperties the application properties
	 * @param ledController the led controller
	 * @param displayController the display controller
	 * @param stateMachine the state machine
	 * @param taskScheduler the task scheduler
	 * @param gameButtonSupplier the game button supplier
	 */
	public MemoryGame(ApplicationProperties applicationProperties, LedController ledController,
			DisplayController displayController, StateMachine<String, String> stateMachine,
			TaskScheduler taskScheduler, Supplier<int[]> gameButtonSupplier) {
		super(applicationProperties, ledController, displayController, stateMachine, taskScheduler, gameButtonSupplier);
	}

	@OnStateEntry(target = Application.STATE_MEMORYGAME_INIT)
	public void initGame() {
		log.info("Enter {}", Application.STATE_MEMORYGAME_INIT);
		if (getScheduledFuture() != null) {
			throw new IllegalStateException("Game is already scheduled");
		}
		queue = gameButtonSupplier.get();
		ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
			}
		}, Duration.ofSeconds(1));
		setScheduledFuture(scheduledFuture);
	}

	@OnStateEntry(target = Application.STATE_MEMORYGAME_PRESS)
	public void button(StateContext<String, String> context) {
		Map<?, ?> properties = context.getMessageHeaders().get(IotStateMachineConstants.IOT_PROPERTIES, Map.class);
		log.debug("properties {}", properties);
		Object id = properties != null ? properties.get("id") : null;
		int button = -1;
		if (id instanceof Integer) {
			button = ((Integer)id).intValue();
		}
		log.info("button {}", button);
	}

}

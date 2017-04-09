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
import java.util.Collection;
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
import org.springframework.util.StringUtils;

/**
 * {@code SpeedGame} runs a game logic where buttons/leds will illuminate with
 * fastening order and player is required to press buttons on same order. Game
 * logic will fail if wrong button in pressed.
 *
 * @author Janne Valkealahti
 *
 */
@WithStateMachine(id = IotStateMachineConstants.ID_STATEMACHINE)
public class SpeedGame extends AbstractGame {

	private static final Logger log = LoggerFactory.getLogger(SpeedGame.class);
	private int[] queue;
	private int tail = -1;
	private int head = 0;

	/**
	 * Instantiates a new speed game.
	 *
	 * @param applicationProperties the application properties
	 * @param ledController the led controller
	 * @param displayController the display controller
	 * @param stateMachine the state machine
	 * @param taskScheduler the task scheduler
	 * @param gameButtonSupplier the game button supplier
	 */
	public SpeedGame(ApplicationProperties applicationProperties, LedController ledController,
			DisplayController displayController, StateMachine<String, String> stateMachine,
			TaskScheduler taskScheduler, Supplier<int[]> gameButtonSupplier) {
		super(applicationProperties, ledController, displayController, stateMachine, taskScheduler, gameButtonSupplier);
	}

	@OnStateEntry(target = Application.STATE_SPEEDGAME_INIT)
	public void initGame() {
		log.info("Enter {}", Application.STATE_SPEEDGAME_INIT);
		if (getScheduledFuture() != null) {
			throw new IllegalStateException("Game is already scheduled");
		}
		tail = -1;
		head = 0;
		queue = getButtonQueue();
		ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (head < queue.length) {
					log.info("pulse {} at {}", queue[head], head);
					ledController.pulse(queue[head++], 200);
				}
			}
		}, Duration.ofSeconds(1));
		setScheduledFuture(scheduledFuture);
	}

	@OnStateEntry(target = Application.STATE_SPEEDGAME_PRESS)
	public void button(StateContext<String, String> context) {
		Collection<?> tags = context.getMessageHeaders().get(IotStateMachineConstants.IOT_TAGS, Collection.class);
		log.info("tags {}", StringUtils.collectionToCommaDelimitedString(tags));
		int button = -1;

		Map<?, ?> properties = context.getMessageHeaders().get(IotStateMachineConstants.IOT_PROPERTIES, Map.class);
		log.debug("properties {}", properties);
		Object id = properties != null ? properties.get("id") : null;
		if (id instanceof Integer) {
			button = ((Integer)id).intValue();
		}

		if (button > 0 && queue[++tail] == button) {
			displayController.setScore(Integer.toString(tail + 1));
		} else {
			stateMachine.sendEvent(Application.EVENT_GAME_END);
		}
	}

	@Override
	public String toString() {
		return "SpeedGame [tail=" + tail + ", head=" + head + "]";
	}
}

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

import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.OnStateEntry;

/**
 * Base support offering facilities for common game features.
 *
 * @author Janne Valkealahti
 *
 */
public class AbstractGame {

	private static final Logger log = LoggerFactory.getLogger(AbstractGame.class);
	protected ApplicationProperties applicationProperties;
	protected LedController ledController;
	protected DisplayController displayController;
	protected StateMachine<String, String> stateMachine;
	protected TaskScheduler taskScheduler;
	protected Supplier<int[]> gameButtonSupplier;
	private ScheduledFuture<?> scheduledFuture;

	/**
	 * Instantiates a new abstract game.
	 *
	 * @param applicationProperties the application properties
	 * @param ledController the led controller
	 * @param displayController the display controller
	 * @param stateMachine the state machine
	 * @param taskScheduler the task scheduler
	 * @param gameButtonSupplier the game button supplier
	 */
	public AbstractGame(ApplicationProperties applicationProperties, LedController ledController,
			DisplayController displayController, StateMachine<String, String> stateMachine,
			TaskScheduler taskScheduler, Supplier<int[]> gameButtonSupplier) {
		this.applicationProperties = applicationProperties;
		this.ledController = ledController;
		this.displayController = displayController;
		this.stateMachine = stateMachine;
		this.taskScheduler = taskScheduler;
		this.gameButtonSupplier = gameButtonSupplier;
	}

	@OnStateEntry(target = Application.STATE_GAME_END)
	public void exitGame() {
		log.info("Ending Game");
		if (scheduledFuture != null) {
			scheduledFuture.cancel(true);
		}
		scheduledFuture = null;
	}

	protected ScheduledFuture<?> getScheduledFuture() {
		return scheduledFuture;
	}

	protected void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
		this.scheduledFuture = scheduledFuture;
	}

	protected int[] getButtonQueue() {
		return gameButtonSupplier.get();
//		int[] queue = new int[applicationProperties.getTotal()];
//		for (int i = 0; i < applicationProperties.getTotal(); i++) {
//			queue[i] = LedButton.random().getValue();
//		}
//		return queue;
	}
}

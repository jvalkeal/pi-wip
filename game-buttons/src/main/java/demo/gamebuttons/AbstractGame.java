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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private LedController ledController;

	@Autowired
	private TaskScheduler taskScheduler;

	@Autowired
	private StateMachine<String, String> stateMachine;

	@Autowired
	private DisplayController displayController;

	@Autowired
	private ApplicationProperties applicationProperties;

	private ScheduledFuture<?> scheduledFuture;

	protected LedController getLedController() {
		return ledController;
	}

	protected TaskScheduler getTaskScheduler() {
		return taskScheduler;
	}

	protected StateMachine<String, String> getStateMachine() {
		return stateMachine;
	}

	protected DisplayController getDisplayController() {
		return displayController;
	}

	protected ScheduledFuture<?> getScheduledFuture() {
		return scheduledFuture;
	}

	protected void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
		this.scheduledFuture = scheduledFuture;
	}

	protected int[] getButtonQueue() {
		int[] queue = new int[applicationProperties.getTotal()];
		for (int i = 0; i < applicationProperties.getTotal(); i++) {
			queue[i] = LedButton.random().getValue();
		}
		return queue;
	}

	@OnStateEntry(target = Application.STATE_GAME_END)
	public void exitGame() {
		log.info("Ending Game");
		if (scheduledFuture != null) {
			scheduledFuture.cancel(true);
		}
		scheduledFuture = null;
	}
}

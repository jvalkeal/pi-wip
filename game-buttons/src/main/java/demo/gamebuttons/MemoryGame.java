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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.OnStateEntry;

/**
 * {@code MemoryGame} runs a game logic where buttons/leds will illuminate with
 * static order and player is required to press buttons on same order. With
 * every cycle one more led is illuminated and user needs to remember order.
 * Game logic will fail if wrong button in pressed. User needs to wait for
 *
 * @author Janne Valkealahti
 *
 */
public class MemoryGame {

	private static final Logger log = LoggerFactory.getLogger(MemoryGame.class);

	@Autowired
	private LedController ledBlinker;

	@Autowired
	private TaskScheduler taskScheduler;

	@Autowired
	private StateMachine<String, String> stateMachine;

	@Autowired
	private ScoreController scoreDisplay;

	@OnStateEntry(target = Application.STATE_MEMORYGAME_INIT)
	public void initGame() {
		log.info("Enter {}", Application.STATE_MEMORYGAME_INIT);
	}
}

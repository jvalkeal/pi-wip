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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.iot.event.EnableIotContextEvents;
import org.springframework.cloud.iot.statemachine.IotStateMachineConstants;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.OnStateEntry;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;

/**
 * Main class handling hardware sequences outside of a running game. Flashes
 * leds when application bootstraps and in idle randomly flashes leds.
 * {@code #runIdleSequence()} and {@code #runIdleSequence()} are driven from a
 * running {@link StateMachine}.
 *
 * @author Janne Valkealahti
 *
 */
@SpringBootApplication
@EnableIotContextEvents
@WithStateMachine(id = IotStateMachineConstants.ID_STATEMACHINE)
@EnableConfigurationProperties(ApplicationProperties.class)
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	// State id's
	public static final String STATE_IDLE = "IDLE";
	public static final String STATE_SPEEDGAME = "SPEEDGAME";
	public static final String STATE_SPEEDGAME_INIT = "SPEEDGAME_INIT";
	public static final String STATE_SPEEDGAME_WAIT = "SPEEDGAME_WAIT";
	public static final String STATE_SPEEDGAME_PRESS = "SPEEDGAME_PRESS";
	public static final String STATE_MEMORYGAME = "MEMORYGAME";
	public static final String STATE_MEMORYGAME_INIT = "MEMORYGAME_INIT";
	public static final String STATE_MEMORYGAME_WAIT = "MEMORYGAME_WAIT";
	public static final String STATE_MEMORYGAME_PRESS = "MEMORYGAME_PRESS";
	public static final String STATE_GAME_END = "GAME_END";

	// Event id's
	public static final String EVENT_GAME_END = "GAME_END";
	public static final String EVENT_BUTTON_RELEASED = "BUTTON_RELEASED";

	@Autowired
	private LedController ledController;

	@Autowired
	private DisplayController displayController;

	@Autowired
	private SoundController soundController;

	@OnTransition(source = "IDLE", target = "IDLE")
	public void runIdleSequence() {
		log.info("runIdleSequence");
		ledController.pulse(LedButton.random().getValue(), 100);
	}

	@OnStateEntry(target = "SYSTEM_INIT")
	public void runSystemInit() {
		log.info("System init, leds should now flash few times with some sounds");
		soundController.sound();
		ledController.pulse(LedButton.LED1.getValue(), 200);
		ledController.pulse(LedButton.LED2.getValue(), 200);
		ledController.pulse(LedButton.LED3.getValue(), 200);
		ledController.pulse(LedButton.LED4.getValue(), 200);
		ledController.pulse(LedButton.getSet(LedButton.LED1, LedButton.LED2, LedButton.LED3, LedButton.LED4), 100);
		ledController.pulse(LedButton.getSet(LedButton.LED1, LedButton.LED2, LedButton.LED3, LedButton.LED4), 100);
		ledController.pulse(LedButton.getSet(LedButton.LED1, LedButton.LED2, LedButton.LED3, LedButton.LED4), 100);
		soundController.sound();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

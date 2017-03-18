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
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.iot.statemachine.IotStateMachineConstants;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.OnStateEntry;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.util.StringUtils;

@WithStateMachine(id = IotStateMachineConstants.ID_STATEMACHINE)
public class SpeedGame {

	private static final Logger log = LoggerFactory.getLogger(SpeedGame.class);
	private int[] queue = new int[12];
	private int tail = -1;
	private int head = 0;
	private ScheduledFuture<?> scheduledFuture;

	@Autowired
	private LedBlinker ledBlinker;

	@Autowired
	private TaskScheduler taskScheduler;

	@Autowired
	private StateMachine<String, String> stateMachine;

	@Autowired
	private ScoreDisplay scoreDisplay;

	@OnStateEntry(target = "SPEEDGAME_INIT")
	public void initGame() {
		log.info("Enter SPEEDGAME_INIT");
		if (scheduledFuture != null) {
			throw new IllegalStateException("Game is already scheduled");
		}
		tail = -1;
		head = 0;
		for (int i = 0; i < 12; i += 4) {
			queue[i] = LedButton.random().getValue();
			queue[i + 1] = LedButton.random().getValue();
			queue[i + 2] = LedButton.random().getValue();
			queue[i + 3] = LedButton.random().getValue();
		}
		scheduledFuture = taskScheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (head < queue.length) {
					log.info("pulse {} at {}", queue[head], head);
					ledBlinker.pulse(queue[head++], 200);
				}
			}
		}, Duration.ofSeconds(1));
	}

	@OnStateEntry(target = "GAME_END")
	public void exitGame() {
		log.info("Ending SpeedGame");
		if (scheduledFuture != null) {
			scheduledFuture.cancel(true);
		}
		scheduledFuture = null;
	}

	@OnStateEntry(target = "SPEEDGAME_PRESS")
	public void button(StateContext<String, String> context) {
		Collection<?> tags = context.getMessageHeaders().get(IotStateMachineConstants.IOT_TAGS, Collection.class);
		log.info("tags {}", StringUtils.collectionToCommaDelimitedString(tags));
		int button = -1;
		if (tags.contains("button1")) {
			button = 0x80;
		} else if (tags.contains("button2")) {
			button = 0x40;
		} else if (tags.contains("button3")) {
			button = 0x20;
		} else if (tags.contains("button4")) {
			button = 0x10;
		}

		if (button > 0 && queue[++tail] == button) {
			scoreDisplay.setScore(Integer.toString(tail + 1));
		} else {
			stateMachine.sendEvent("GAME_END");
		}
	}

	@Override
	public String toString() {
		return "SpeedGame [tail=" + tail + ", head=" + head + "]";
	}
}

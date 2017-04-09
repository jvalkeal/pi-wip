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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cloud.iot.statemachine.IotStateMachineAutoConfiguration;
import org.springframework.cloud.iot.statemachine.IotStateMachineConstants;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;

public class StateMachineTests {

	private AnnotationConfigApplicationContext context;

	@Before
	public void setup() {
		context = new AnnotationConfigApplicationContext();
		context.register(StateMachineConfig.class, IotStateMachineAutoConfiguration.class);
		context.refresh();
	}

	@After
	public void clean() {
		if (context != null) {
			context.close();
		}
		context = null;
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSpeedGame() throws Exception {
		StateMachine<String, String> stateMachine = context.getBean(StateMachine.class);
		StateMachineTestPlan<String, String> plan =
				StateMachineTestPlanBuilder.<String, String>builder()
					.stateMachine(stateMachine)
					.step()
						.expectStateChanged(1)
						.expectState(Application.STATE_IDLE)
						.and()
					.step()
						.sendEvent(Application.EVENT_BUTTON_RELEASED)
						.expectStateChanged(3)
						.expectStates(Application.STATE_SPEEDGAME, Application.STATE_SPEEDGAME_WAIT)
						.and()
					.step()
						.sendEvent(Application.EVENT_GAME_END)
						.expectStateChanged(2)
						.expectState(Application.STATE_IDLE)
						.and()
					.build();
		plan.test();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMemoryGame() throws Exception {

		MessageBuilder<String> builder = MessageBuilder.withPayload(Application.EVENT_BUTTON_RELEASED);
		Collection<String> tags = new ArrayList<>();
		tags.add("button3");
		builder.setHeader(IotStateMachineConstants.IOT_TAGS, tags);
		Map<String, Object> properties = new HashMap<>();
		properties.put("id", 0x20);
		builder.setHeader(IotStateMachineConstants.IOT_PROPERTIES, properties);
		Message<String> message = builder.build();

		StateMachine<String, String> stateMachine = context.getBean(StateMachine.class);
		StateMachineTestPlan<String, String> plan =
				StateMachineTestPlanBuilder.<String, String>builder()
					.stateMachine(stateMachine)
					.step()
						.expectStateChanged(1)
						.expectState(Application.STATE_IDLE)
						.and()
					.step()
						.sendEvent(message)
						.expectStateChanged(3)
						.expectStates(Application.STATE_MEMORYGAME, Application.STATE_MEMORYGAME_WAIT)
						.and()
					.step()
						.sendEvent(Application.EVENT_GAME_END)
						.expectStateChanged(2)
						.expectState(Application.STATE_IDLE)
						.and()
					.build();
		plan.test();
	}

}

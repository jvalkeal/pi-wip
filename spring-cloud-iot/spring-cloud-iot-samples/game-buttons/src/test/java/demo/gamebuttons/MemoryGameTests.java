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

import java.util.function.Supplier;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.statemachine.StateMachine;

public class MemoryGameTests {

	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleGameLogic() {
		ApplicationProperties applicationProperties = new ApplicationProperties();
		LedController ledController = Mockito.mock(LedController.class);
		DisplayController displayController = Mockito.mock(DisplayController.class);
		StateMachine stateMachine = Mockito.mock(StateMachine.class);
		TaskScheduler taskScheduler = Mockito.mock(TaskScheduler.class);

		Supplier<int[]> supplier = () -> {
			int[] buttons = new int[4];
			buttons[0] = LedButton.LED1.getValue();
			buttons[1] = LedButton.LED2.getValue();
			buttons[2] = LedButton.LED3.getValue();
			buttons[3] = LedButton.LED4.getValue();
			return buttons;
		};

		MemoryGame memoryGame = new MemoryGame(applicationProperties, ledController, displayController, stateMachine,
				taskScheduler, supplier);
		memoryGame.initGame();
	}
}

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

import java.util.Collection;

import org.springframework.cloud.iot.statemachine.IotStateMachineConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.guard.Guard;

/**
 * Configuration for {@link StateMachine} objects. Keeping things here allows to
 * test machine without instantiating full application
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
public class StateMachineConfig {

	@Bean
	public Guard<String, String> gameChoiceGuard() {
		return new Guard<String, String>() {

			@Override
			public boolean evaluate(StateContext<String, String> context) {
				// evaluate true if either button3 or button4 is present
				Collection<?> tags = context.getMessageHeaders().get(IotStateMachineConstants.IOT_TAGS,
						Collection.class);
				return tags != null && (tags.contains("button3") || tags.contains("button4"));
			}
		};
	}
}

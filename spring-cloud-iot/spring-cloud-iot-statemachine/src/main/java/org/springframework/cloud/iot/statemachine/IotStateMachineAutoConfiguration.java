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
package org.springframework.cloud.iot.statemachine;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.cloud.iot.event.IotEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineModelConfigurer;
import org.springframework.statemachine.config.model.StateMachineModelFactory;
import org.springframework.statemachine.uml.UmlStateMachineModelFactory;

/**
 * Auto-config for {@link StateMachine} which can be used to centrally handle
 * {@link IotEvent} context events.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
@ConditionalOnResource(resources = "${spring.cloud.iot.statemachine.location:classpath:iot-statemachine.uml}")
public class IotStateMachineAutoConfiguration {

	@Configuration
	@EnableStateMachine(name = IotStateMachineConstants.ID_STATEMACHINE)
	public static class IotStateMachineConfig extends StateMachineConfigurerAdapter<String, String> {

		@Value("${spring.cloud.iot.statemachine.location:classpath:iot-statemachine.uml}")
		private String location;

		@Override
		public void configure(StateMachineModelConfigurer<String, String> model) throws Exception {
			model
				.withModel()
					.factory(modelFactory());
		}

		@Override
		public void configure(StateMachineConfigurationConfigurer<String, String> config) throws Exception {
			config
				.withConfiguration()
					.autoStartup(true);
		}

		@Bean
		public StateMachineModelFactory<String, String> modelFactory() {
			return new UmlStateMachineModelFactory(location);
		}
	}

	@Configuration
	public static class IotStateMachineEventDispatcherConfig {

		@Bean
		public IotStateMachineEventDispatcher iotStateMachineEventDispatcher(
				@Qualifier(IotStateMachineConstants.ID_STATEMACHINE) StateMachine<String, String> stateMachine) {
			return new IotStateMachineEventDispatcher(stateMachine);
		}
	}
}

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

import java.util.Date;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

/**
 * {@link Trigger} implementation which can define number of steps it will
 * trigger and periods with different trigger times. Can be useful in cases
 * where you need 100 executions and with every 10 executions next trigger
 * should happen faster.
 *
 * @author Janne Valkealahti
 *
 */
public class StepPeriodTrigger implements Trigger {

	@Override
	public Date nextExecutionTime(TriggerContext arg0) {
		return null;
	}

}

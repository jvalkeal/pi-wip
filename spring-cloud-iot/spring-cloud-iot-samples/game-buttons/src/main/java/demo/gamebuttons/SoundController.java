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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.iot.component.sound.ActiveBuzzer;
import org.springframework.cloud.iot.pi4j.support.Pi4jUtils;
import org.springframework.stereotype.Component;

/**
 * {@link Component @Component} which is getting component scanned and
 * automatically created as a bean. Sole purpose of this class is to handle
 * sounds emitted from a games.
 *
 * @author Janne Valkealahti
 *
 */
@Component
public class SoundController {

	@Autowired
	private ActiveBuzzer buzzer;

	public void sound() {
		buzzer.setEnabled(true);
		Pi4jUtils.delay(500);
		buzzer.setEnabled(false);
	}
}

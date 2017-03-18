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
package demo.sunfoundersensor10active;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.iot.component.sound.ActiveBuzzer;
import org.springframework.cloud.iot.pi4j.support.Pi4jUtils;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
public class Application {

	@Autowired
	private ActiveBuzzer buzzer;

	@Scheduled(fixedRate=1000)
	public void beep() {
		buzzer.setEnabled(true);
		Pi4jUtils.delay(500);
		buzzer.setEnabled(false);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

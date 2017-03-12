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

import java.util.Random;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.iot.component.Button;
import org.springframework.cloud.iot.component.Led;
import org.springframework.cloud.iot.event.ButtonEvent;
import org.springframework.cloud.iot.event.EnableIotContextEvents;
import org.springframework.cloud.iot.listener.ButtonListenerAdapter;
import org.springframework.cloud.iot.pi4j.support.Pi4jUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableIotContextEvents
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	@Qualifier("GPIO_led1")
	private Led led1;

	@Autowired
	@Qualifier("GPIO_led2")
	private Led led2;

	@Autowired
	@Qualifier("GPIO_led3")
	private Led led3;

	@Autowired
	@Qualifier("GPIO_led4")
	private Led led4;

	@Autowired
	@Qualifier("GPIO_button1")
	private Button button1;

	@Autowired
	@Qualifier("GPIO_button2")
	private Button button2;

	@Autowired
	@Qualifier("GPIO_button3")
	private Button button3;

	@Autowired
	@Qualifier("GPIO_button4")
	private Button button4;

	@Scheduled(initialDelay = 2000, fixedDelay = 500)
	public void updateLeds() {
		Random generator = new Random();
		int i = generator.nextInt(4);
		led1.setEnabled(i != 0);
		led2.setEnabled(i != 1);
		led3.setEnabled(i != 2);
		led4.setEnabled(i != 3);

		Pi4jUtils.delay(200);
		led1.setEnabled(true);
		led2.setEnabled(true);
		led3.setEnabled(true);
		led4.setEnabled(true);
	}

	@EventListener
	public void buttonEvent(ButtonEvent event) {
		log.info("{}", event);
	}

	@PostConstruct
	public void setup() {
		button1.addButtonListener(new ButtonListenerAdapter() {
			@Override
			public void onReleased() {
				log.info("button1");
			}
		});
		button2.addButtonListener(new ButtonListenerAdapter() {
			@Override
			public void onReleased() {
				log.info("button2");
			}
		});
		button3.addButtonListener(new ButtonListenerAdapter() {
			@Override
			public void onReleased() {
				log.info("button3");
			}
		});
		button4.addButtonListener(new ButtonListenerAdapter() {
			@Override
			public void onReleased() {
				log.info("button4");
			}
		});
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

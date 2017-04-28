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
package demo.sunfoundersensor15joystick;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.iot.component.joystick.Joystick;
import org.springframework.cloud.iot.event.JoystickEvent;
import org.springframework.cloud.iot.listener.JoystickListener;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

@SpringBootApplication
public class Application {

	private final static Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private Joystick joyStick;

	@PostConstruct
	public void setup() throws IOException, UnsupportedBusNumberException, InterruptedException {
		joyStick.addJoystickListener(new JoystickListener() {

			@Override
			public void onJoystickEvent(JoystickEvent event) {
				log.info("Received new event {}", event);
			}
		});
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

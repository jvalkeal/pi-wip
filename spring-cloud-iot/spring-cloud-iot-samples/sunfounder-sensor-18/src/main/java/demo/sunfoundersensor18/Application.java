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
package demo.sunfoundersensor18;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.iot.component.TemperatureSensor;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private final static Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private TemperatureSensor sensor;

	@Override
	public void run(String... args) throws Exception {
		while(true) {
			log.info("Temperature {}", sensor.getTemperature());
			Thread.sleep(500);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}


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
package demo.sunfoundersuper11segment;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.iot.component.ShiftRegister;
import org.springframework.cloud.iot.component.display.SevenSegmentDisplay;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private final static Logger log = LoggerFactory.getLogger(Application.class);
	private final char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f'};

	@Autowired
	private ShiftRegister shiftRegister;

	@Override
	public void run(String... args) throws Exception {
		SevenSegmentDisplay segmentDisplay = new SevenSegmentDisplay(shiftRegister);
		boolean dot = false;

		for (int i = 0; i < 10; i++) {
			segmentDisplay.setInt(i, dot = !dot);
			Thread.sleep(500);
		}

		for (int i = 0; i < chars.length; i++) {
			segmentDisplay.setChar(chars[i], dot = !dot);
			Thread.sleep(500);
		}
	}

	@PreDestroy
	public void clean() {
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

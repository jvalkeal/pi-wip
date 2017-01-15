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
	private final int[] segCode = new int[] { 0x3f, 0x06, 0x5b, 0x4f, 0x66, 0x6d, 0x7d, 0x07, 0x7f, 0x6f, 0x77, 0x7c, 0x39, 0x5e, 0x79,
			0x71, 0x80 };
//	private final int[] segCode = new int[] { 0x3F, 0x06, 0x5B, 0x4F, 0x66, 0x6D, 0x7D, 0x07, 0x7F, 0x6F };
//	private final int[] segCode = new int[] { 0x7F };
	private final char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f'};

	@Autowired
	private ShiftRegister shiftRegister;

	@Override
	public void run(String... args) throws Exception {
		SevenSegmentDisplay d = new SevenSegmentDisplay(shiftRegister);

		for (int i = 0; i < 10; i++) {
			d.setInt(i);
			Thread.sleep(500);
		}

		for (int i = 0; i < chars.length; i++) {
			d.setChar(chars[i]);
			Thread.sleep(500);
		}

//		for (int j = 0; j < segCode.length; j++) {
//			shiftRegister.shift(segCode[j]);
//			shiftRegister.store();
//			Thread.sleep(500);
//		}


//		int[] bits = new int[8];
//		for (int j = 0; j < segCode.length; j++) {
//			for (int i = 0; i < 8; i++) {
//				bits[i] = (0x80 & (segCode[j] << i));
//			}
//			shiftRegister.shift(bits);
//			shiftRegister.store();
//			Thread.sleep(500);
//		}
	}

	@PreDestroy
	public void clean() {
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

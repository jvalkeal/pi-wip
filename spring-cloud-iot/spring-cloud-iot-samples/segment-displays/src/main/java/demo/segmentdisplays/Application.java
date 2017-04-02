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
package demo.segmentdisplays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private final static Logger log = LoggerFactory.getLogger(Application.class);
//	private int[] intMapping = new int[] { 0x3F, 0x06, 0x5B, 0x4F, 0x66, 0x6D, 0x7D, 0x07, 0x7F, 0x6F };
	private int[][] commands = new int[][] {
			// All LED segments should light up
			{ 0x0F, 0x01 },
			{ 0x0F, 0x00 },

			// Enable mode B
			{ 0x09, 0x01 }, //Code B decode for digit 0
//			{ 0x09, 0xFF }, //Code B decode for digits 7–0
			// Use lowest intensity
			{ 0x0A, 0x00 },
			// Only scan one digit
			{ 0x0B, 0x00 }, //Display digit 0 only*
			// Turn on chip
			{ 0x0C, 0x01 } //Normal operation
		};

	@Override
	public void run(String... args) throws Exception {
		SpiDevice spi = SpiFactory.getInstance(SpiChannel.CS0, SpiDevice.DEFAULT_SPI_SPEED,
				SpiDevice.DEFAULT_SPI_MODE);

		for (int i = 0; i < commands[0].length; i++) {
			byte data = (byte)commands[0][i];
			log.info("XX writing {}", data);
			byte[] write = spi.write(data);
			log.info("XX got {}", write);
		}

		Thread.sleep(1000);
		for (int j = 1; j < commands.length; j++) {
			for (int i = 0; i < commands[j].length; i++) {
				byte data = (byte)commands[j][i];
				log.info("XX writing {}", data);
				byte[] write = spi.write(data);
				log.info("XX got {}", write);
			}
		}

		for (int j = 0; j < 1000; j++) {
			for (int i = 0; i < 10; i++) {
				byte data = (byte)0x01;
				log.info("XX writing {}", data);
				spi.write(data);
				data = (byte)i;
				log.info("XX writing {}", data);
				spi.write(data);
				Thread.sleep(100);
			}
		}


	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

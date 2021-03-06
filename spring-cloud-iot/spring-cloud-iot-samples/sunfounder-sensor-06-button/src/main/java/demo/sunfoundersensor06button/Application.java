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
package demo.sunfoundersensor06button;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.iot.component.Button;
import org.springframework.cloud.iot.component.DimmedLed;
import org.springframework.cloud.iot.listener.ButtonListener;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private static final int[] colors = new int[] { 0xFF00, 0x00FF, 0x0FF0, 0xF00F };

	@Autowired
	@Qualifier("GPIO_myButton")
	private Button button;

	@Autowired
	@Qualifier("GPIO_myDimmedLedRed")
	private DimmedLed ledR;

	@Autowired
	@Qualifier("GPIO_myDimmedLedGreen")
	private DimmedLed ledG;

	@Override
	public void run(String... args) throws Exception {
		button.addButtonListener(new ButtonListener() {

			@Override
			public void onReleased() {
				int R_val = colors[0]  >> 8;
				int G_val = colors[0] & 0x00FF;
				ledR.setPercentage(mapColor(R_val, 0, 255, 0, 100));
				ledG.setPercentage(mapColor(G_val, 0, 255, 0, 100));
			}

			@Override
			public void onPressed() {
				int R_val = colors[1]  >> 8;
				int G_val = colors[1] & 0x00FF;
				ledR.setPercentage(mapColor(R_val, 0, 255, 0, 100));
				ledG.setPercentage(mapColor(G_val, 0, 255, 0, 100));
			}
		});
	}

	@PreDestroy
	public void clean() {
		ledR.setEnabled(false);
		ledG.setEnabled(false);
	}

	private static int mapColor(int x, int in_min, int in_max, int out_min, int out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

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
package demo.sunfoundersensor06;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.iot.component.Button;
import org.springframework.cloud.iot.component.DimmedLed;
import org.springframework.cloud.iot.listener.ButtonListener;

import com.pi4j.wiringpi.GpioInterrupt;
import com.pi4j.wiringpi.GpioInterruptEvent;
import com.pi4j.wiringpi.GpioInterruptListener;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private static final int[] colors = new int[] { 0xFF00, 0x00FF, 0x0FF0, 0xF00F };
	private final static Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	@Qualifier("GPIO_0")
//	@Qualifier("GPIO_17")
	private Button button;

	@Autowired
	@Qualifier("GPIO_1")
//	@Qualifier("GPIO_18")
	private DimmedLed ledR;

	@Autowired
	@Qualifier("GPIO_2")
//	@Qualifier("GPIO_27")
	private DimmedLed ledG;

	@Override
	public void run(String... args) throws Exception {
//		for (int color : colors) {
//			int R_val = color  >> 8;
//			int G_val = color & 0x00FF;
//			ledR.setPercentage(mapColor(R_val, 0, 255, 0, 100));
//			ledG.setPercentage(mapColor(G_val, 0, 255, 0, 100));
//			Thread.sleep(500);
//		}
//		ledR.setEnabled(false);
//		ledG.setEnabled(false);

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

//		GpioInterrupt.enablePinStateChangeCallback(0);
//		GpioInterrupt.enablePinStateChangeCallback(17);
//
//		GpioInterrupt.addListener(new GpioInterruptListener() {
//			@Override
//			public void pinStateChange(GpioInterruptEvent event) {
//				log.info("GpioInterruptEvent {} {} {}", event.getPin(), event.getState(), event.getStateValue());
//			}
//		});
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

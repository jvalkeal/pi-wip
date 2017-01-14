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
package demo.sunfoundersensor27;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.iot.component.Button;
import org.springframework.cloud.iot.component.IncrementalRotary;
import org.springframework.cloud.iot.listener.ButtonListener;
import org.springframework.cloud.iot.listener.RotaryListener;
import org.springframework.cloud.iot.support.RotaryWaveform;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiBcmPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private final static Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private Button button;

	@Autowired
	private IncrementalRotary rotary;

	@Override
	public void run(String... args) throws Exception {
		button.addButtonListener(new ButtonListener() {

			@Override
			public void onReleased() {
			}

			@Override
			public void onPressed() {
				rotary.reset();
			}
		});

		rotary.addRotaryListener(new RotaryListener() {

			@Override
			public void onRotate() {
				log.info("Counter {}", rotary.getCounter());
			}
		});
	}

	@PreDestroy
	public void clean() {
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

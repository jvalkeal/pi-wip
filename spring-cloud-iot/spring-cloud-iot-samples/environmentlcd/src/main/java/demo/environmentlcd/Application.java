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
package demo.environmentlcd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.iot.component.Lcd;
import org.springframework.cloud.iot.component.sensor.HumiditySensor;
import org.springframework.cloud.iot.component.sensor.TemperatureSensor;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private final static Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private Lcd lcd;

	@Autowired
	private TemperatureSensor temperature;

	@Autowired
	private HumiditySensor humidity;

	@Override
	public void run(String... args) throws Exception {
		Flux.combineLatest(temperature.getTemperature().asFlux(), humidity.getHumidity().asFlux(),
				(t, h) -> {
					log.info("New temperature {} humidity {}", t, h);
					return new Double[] { t, h };
				}).subscribe(a -> {
//					lcd.setText(String.format("T: %.1f H: %.1f", a[0], a[1]));
					lcd.setText("temperatureText", String.format("%.1f", a[0]));
					lcd.setText("humidityText", String.format("%.1f", a[1]));
				});
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

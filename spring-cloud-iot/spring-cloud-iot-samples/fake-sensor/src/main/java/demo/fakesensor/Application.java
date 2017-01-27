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
package demo.fakesensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.iot.component.TemperatureSensor;
import org.springframework.cloud.iot.gateway.EnableIotGatewayClient;
import org.springframework.context.annotation.Bean;

import reactor.core.Exceptions;

@EnableIotGatewayClient
@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private SensorGateway sensorGateway;

	@Bean
	public TemperatureSensor fakeTemperatureSensor() {
		return new FakeTemperatureSensor();
	}

	@Override
	public void run(String... args) throws Exception {
		fakeTemperatureSensor().temperatureAsFlux().subscribe(t -> {
			try {
				sensorGateway.sendSensorValue(Double.toString(t), "/path");
			} catch (Exception e) {
				Exceptions.propagate(e);
			}
		});
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

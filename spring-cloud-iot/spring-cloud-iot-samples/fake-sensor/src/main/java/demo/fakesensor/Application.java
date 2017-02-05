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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.iot.component.sensor.TemperatureSensor;
import org.springframework.cloud.iot.gateway.EnableIotGatewayClient;
import org.springframework.cloud.iot.test.fake.EnableIotFakeSensors;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

@EnableIotFakeSensors
//@EnableIotGatewayClient
@SpringBootApplication
public class Application implements CommandLineRunner {

	private final static Logger log = LoggerFactory.getLogger(Application.class);

//	@Autowired
//	private TemperatureSensor fakeTemperatureSensor;
//
//	@Autowired
//	private MessageChannel iotSensorValueChannel;

	@Override
	public void run(String... args) throws Exception {

		// stream completes after 5 values has been taken
//		fakeTemperatureSensor.temperatureAsFlux()
//			.subscribe(t -> {
//				log.info("fakeTemperatureSensor {}", t);
//				iotSensorValueChannel.send(MessageBuilder.withPayload(t).build());
//			});
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

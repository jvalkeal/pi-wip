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
package demo.temperaturelcd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.metrics.writer.MessageChannelMetricWriter;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.iot.component.Lcd;
import org.springframework.cloud.iot.component.TemperatureSensor;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private final static Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private Lcd lcd;

	@Autowired
	private TemperatureSensor sensor;

	@Override
	public void run(String... args) throws Exception {
		sensor.temperatureAsFlux().subscribe(t -> {
			log.info("New temperature {}", t);
			lcd.setText(Double.toString(t));
		});
	}

	@Bean
	public MqttPahoClientFactory mqttClientFactory() {
		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		factory.setServerURIs("tcp://192.168.1.96:1883");
		factory.setUserName("scdf");
		factory.setPassword("scdf");
		return factory;
	}

	@Bean
	@Transformer(inputChannel = "mqttOutboundChannel", outputChannel = "convertedMqttOutboundChannel")
	public ObjectToJsonTransformer metricToJsonTransformer() {
		return new ObjectToJsonTransformer();
	}

	@Bean
	@ServiceActivator(inputChannel = "convertedMqttOutboundChannel")
	public MessageHandler mqttOutbound() {
		MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("testClient", mqttClientFactory());
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic("testTopic");
		return messageHandler;
	}

	@Bean
	public MessageChannel mqttOutboundChannel() {
		return new DirectChannel();
	}

	@Bean
	@ExportMetricWriter
	public MetricWriter metricWriter() {
		return new MessageChannelMetricWriter(mqttOutboundChannel());
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

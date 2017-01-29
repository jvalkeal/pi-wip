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
package demo.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.iot.boot.properties.MqttConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;

@SpringBootApplication
public class Application {

	@EnableConfigurationProperties(MqttConfigurationProperties.class)
	@Configuration
	public static class MQTTConfig {

		@Autowired
		private MqttConfigurationProperties properties;

		@Bean
		public MqttPahoClientFactory mqttClientFactory() {
			DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
			factory.setServerURIs(properties.determineAddresses().toArray(new String[0]));
			factory.setUserName(properties.determineUsername());
			factory.setPassword(properties.determineUsername());
			return factory;
		}

		@Bean
		public MessageProducerSupport mqttInbound() {
			MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("metricTopicConsumer", mqttClientFactory(),
					"metricTopic");
			adapter.setCompletionTimeout(5000);
			adapter.setConverter(new DefaultPahoMessageConverter());
			adapter.setQos(1);
			return adapter;
		}

		@Bean
		public IntegrationFlow mqttInFlow() {
			return IntegrationFlows.from(mqttInbound())
					.handle(logger())
					.get();
		}

		private LoggingHandler logger() {
			LoggingHandler loggingHandler = new LoggingHandler("INFO");
			loggingHandler.setLoggerName("mqtt");
			return loggingHandler;
		}

	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

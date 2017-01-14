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
package demo.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import demo.gateway.Application.MQTTConfig.MyGateway;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private MyGateway myGateway;

	@Override
	public void run(String... args) throws Exception {
		myGateway.sendToMqtt("hello");
	}

	@Configuration
	public static class MQTTConfig {
		@Bean
		public MqttPahoClientFactory mqttClientFactory() {
			DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
			factory.setServerURIs("tcp://192.168.1.96:1883");
			factory.setUserName("scdf");
			factory.setPassword("scdf");
			return factory;
		}

		@Bean
		public MessageProducerSupport mqttInbound() {
			MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("testTopicConsumer",
					mqttClientFactory(), "testTopic");
			adapter.setCompletionTimeout(5000);
			adapter.setConverter(new DefaultPahoMessageConverter());
			adapter.setQos(1);
			return adapter;
		}


		@Bean
		@ServiceActivator(inputChannel = "mqttOutboundChannel")
		public MessageHandler mqttOutbound() {
			MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("testClient", mqttClientFactory());
			messageHandler.setAsync(true);
			messageHandler.setDefaultTopic("testTopic");
			return messageHandler;
		}

		@Bean
		public IntegrationFlow mqttInFlow() {
			return IntegrationFlows.from(mqttInbound())
					.transform(p -> p + ", received from MQTT")
					.handle(logger())
					.get();
		}

		private LoggingHandler logger() {
			LoggingHandler loggingHandler = new LoggingHandler("INFO");
			loggingHandler.setLoggerName("siSample");
			return loggingHandler;
		}

		@Bean
		public MessageChannel mqttOutboundChannel() {
			return new DirectChannel();
		}

		@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
		public interface MyGateway {

			void sendToMqtt(String data);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

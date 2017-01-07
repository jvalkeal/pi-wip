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
package org.springframework.cloud.iot.boot.autoconfigure.metrics;

import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.metrics.writer.MessageChannelMetricWriter;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@ConditionalOnClass(ExportMetricWriter.class)
public class MetricExporterAutoConfiguration {

	@Configuration
	@ConditionalOnClass(MqttPahoClientFactory.class)
	@ConditionalOnProperty(prefix = "spring.cloud.iot.metrics.mqtt.export", name = "enabled", havingValue = "true", matchIfMissing = false)
	public static class MqttConfiguration {

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
	}

}

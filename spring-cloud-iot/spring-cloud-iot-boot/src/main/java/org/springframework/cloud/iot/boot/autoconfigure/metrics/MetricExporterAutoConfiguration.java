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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.iot.boot.properties.MqttConfigurationProperties;
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

/**
 * Boot auto-config for {@link ExportMetricWriter} enabling export
 * into Spring Integration MQTT channel.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
@ConditionalOnClass(ExportMetricWriter.class)
public class MetricExporterAutoConfiguration {

	@Configuration
	@ConditionalOnClass(MqttPahoClientFactory.class)
	@ConditionalOnProperty(prefix = "spring.cloud.iot.metrics.mqtt.export", name = "enabled", havingValue = "true", matchIfMissing = false)
	@EnableConfigurationProperties(MqttConfigurationProperties.class)
	public static class IotMetricExporterMqttConfiguration {

		private final MqttConfigurationProperties properties;

		public IotMetricExporterMqttConfiguration(MqttConfigurationProperties properties) {
			this.properties = properties;
		}

		@Bean
		public MqttPahoClientFactory mqttClientFactory() {
			DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
			factory.setServerURIs(properties.determineAddresses().toArray(new String[0]));
			factory.setUserName(properties.determineUsername());
			factory.setPassword(properties.determineUsername());
			return factory;
		}

		@Bean
		@Transformer(inputChannel = "iotMetricWriterMqttOutboundChannel", outputChannel = "convertedIotMetricWriterMqttOutboundChannel")
		public ObjectToJsonTransformer metricToJsonTransformer() {
			return new ObjectToJsonTransformer();
		}

		@Bean
		@ServiceActivator(inputChannel = "convertedIotMetricWriterMqttOutboundChannel")
		public MessageHandler mqttOutbound() {
			MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("springCloudIotClient", mqttClientFactory());
			messageHandler.setAsync(true);
			messageHandler.setDefaultTopic("metricTopic");
			return messageHandler;
		}

		@Bean
		public MessageChannel iotMetricWriterMqttOutboundChannel() {
			return new DirectChannel();
		}

		@Bean
		@ExportMetricWriter
		public MetricWriter metricWriter() {
			return new MessageChannelMetricWriter(iotMetricWriterMqttOutboundChannel());
		}
	}

}

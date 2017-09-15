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

import java.net.URI;
import java.net.URISyntaxException;

//import org.springframework.boot.actuate.autoconfigure.metrics.ExportMetricWriter;
//import org.springframework.boot.actuate.metrics.Metric;
//import org.springframework.boot.actuate.metrics.writer.Delta;
//import org.springframework.boot.actuate.metrics.writer.MessageChannelMetricWriter;
//import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.iot.boot.properties.CoapConfigurationProperties;
import org.springframework.cloud.iot.boot.properties.MqttConfigurationProperties;
import org.springframework.cloud.iot.gateway.service.metric.MetricGatewayService;
import org.springframework.cloud.iot.gateway.service.metric.MetricGatewayServiceRequest;
import org.springframework.cloud.iot.integration.coap.dsl.Coap;
import org.springframework.cloud.iot.integration.xbee.dsl.XBee;
import org.springframework.cloud.iot.xbee.XBeeSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for
 * ExportMetricWriter enabling export into Spring Integration channels
 * based on MQTT, CoAP and XBee.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
//@ConditionalOnClass(ExportMetricWriter.class)
public class MetricExporterAutoConfiguration {

//	@Configuration
//	@ConditionalOnClass(MetricGatewayService.class)
//	@ConditionalOnBean(MetricGatewayService.class)
//	@ConditionalOnProperty(prefix = "spring.cloud.iot.metrics.gateway.export", name = "enabled", havingValue = "true", matchIfMissing = false)
//	public static class IotMetricExporterGatewayConfiguration {
//
//		private final MetricGatewayService metricGatewayService;
//
//		public IotMetricExporterGatewayConfiguration(MetricGatewayService metricGatewayService) {
//			this.metricGatewayService = metricGatewayService;
//		}
//
//		@Bean
//		@ExportMetricWriter
//		public MetricWriter iotGatewayMetricWriter() {
//			return new MetricWriter() {
//
//				@Override
//				public void reset(String metricName) {
//				}
//
//				@Override
//				public void increment(Delta<?> delta) {
//				}
//
//				@Override
//				public void set(Metric<?> value) {
//					metricGatewayService.publish(new MetricGatewayServiceRequest(value.getName(), value.getValue()));
//				}
//			};
//		}
//	}
//
//	@Configuration
//	@ConditionalOnClass(MqttPahoClientFactory.class)
//	@ConditionalOnProperty(prefix = "spring.cloud.iot.metrics.mqtt.export", name = "enabled", havingValue = "true", matchIfMissing = false)
//	@EnableConfigurationProperties(MqttConfigurationProperties.class)
//	public static class IotMetricExporterMqttConfiguration {
//
//		private final MqttConfigurationProperties properties;
//
//		public IotMetricExporterMqttConfiguration(MqttConfigurationProperties properties) {
//			this.properties = properties;
//		}
//
//		@Bean
//		public MqttPahoClientFactory mqttClientFactory() {
//			DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
//			factory.setServerURIs(properties.determineAddresses().toArray(new String[0]));
//			factory.setUserName(properties.determineUsername());
//			factory.setPassword(properties.determineUsername());
//			return factory;
//		}
//
//		@Bean
//		@Transformer(inputChannel = "iotMetricWriterMqttOutboundChannel", outputChannel = "convertedIotMetricWriterMqttOutboundChannel")
//		public ObjectToJsonTransformer metricToJsonTransformer() {
//			return new ObjectToJsonTransformer();
//		}
//
//		@Bean
//		@ServiceActivator(inputChannel = "convertedIotMetricWriterMqttOutboundChannel")
//		public MessageHandler mqttOutbound() {
//			MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("springCloudIotClient", mqttClientFactory());
//			messageHandler.setAsync(true);
//			messageHandler.setDefaultTopic("metricTopic");
//			return messageHandler;
//		}
//
//		@Bean
//		public MessageChannel iotMetricWriterMqttOutboundChannel() {
//			return new DirectChannel();
//		}
//
//		@Bean
//		@ExportMetricWriter
//		public MetricWriter iotMqttMetricWriter() {
//			return new MessageChannelMetricWriter(iotMetricWriterMqttOutboundChannel());
//		}
//	}
//
//	@Configuration
//	@ConditionalOnClass(Coap.class)
//	@ConditionalOnProperty(prefix = "spring.cloud.iot.metrics.coap.export", name = "enabled", havingValue = "true", matchIfMissing = false)
//	@EnableConfigurationProperties(CoapConfigurationProperties.class)
//	public static class IotMetricExporterCoapConfiguration {
//
//		private final CoapConfigurationProperties properties;
//
//		public IotMetricExporterCoapConfiguration(CoapConfigurationProperties properties) {
//			this.properties = properties;
//		}
//
//		@Bean
//		public MessageChannel iotMetricWriterCoapOutboundChannel() {
//			return new DirectChannel();
//		}
//
//		@Bean
//		public IntegrationFlow iotMetricWriterCoapOutboundFlow() throws URISyntaxException {
//			return IntegrationFlows
//				.from(iotMetricWriterCoapOutboundChannel())
//				.transform(new ObjectToJsonTransformer())
//				.handle(Coap
//						.outboundGateway(new URI("coap", null, "localhost", 5683, "/spring-integration-coap", null, null))
//						.expectReply(false))
//				.get();
//		}
//
//		@Bean
//		@ExportMetricWriter
//		public MetricWriter iotCoapMetricWriter() {
//			return new MessageChannelMetricWriter(iotMetricWriterCoapOutboundChannel());
//		}
//	}
//
//	@Configuration
//	@ConditionalOnClass(XBee.class)
//	@ConditionalOnProperty(prefix = "spring.cloud.iot.metrics.xbee.export", name = "enabled", havingValue = "true", matchIfMissing = false)
//	@EnableConfigurationProperties(CoapConfigurationProperties.class)
//	public static class IotMetricExporterXBeeConfiguration {
//
//		private final XBeeSender xbeeSender;
//
//		public IotMetricExporterXBeeConfiguration(XBeeSender xbeeSender) {
//			this.xbeeSender = xbeeSender;
//		}
//
//		@Bean
//		public MessageChannel iotMetricWriterXBeeOutboundChannel() {
//			return new DirectChannel();
//		}
//
//		@Bean
//		public IntegrationFlow iotMetricWriterXBeeOutboundFlow() throws URISyntaxException {
//			return IntegrationFlows
//				.from(iotMetricWriterXBeeOutboundChannel())
//				.transform(new ObjectToJsonTransformer())
//				.handle(XBee
//						.outboundGateway(xbeeSender))
//				.get();
//		}
//
//		@Bean
//		@ExportMetricWriter
//		public MetricWriter iotXBeeMetricWriter() {
//			return new MessageChannelMetricWriter(iotMetricWriterXBeeOutboundChannel());
//		}
//	}
}

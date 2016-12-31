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
package org.springframework.cloud.iot.boot.autoconfigure;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.cloud.iot.component.TemperatureSensor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class SensorMetricsAutoConfiguration {

	@Configuration
	public static class MetricsConfiguration {
		private final GaugeService gaugeService;

		public MetricsConfiguration(ObjectProvider<GaugeService> gaugeServiceProvider) {
			this.gaugeService = gaugeServiceProvider.getIfAvailable();
		}

		@Bean
		public MetricAdder metricAdder() {
			return new MetricAdder(gaugeService);
		}
	}

	public static class MetricAdder {

		@Autowired(required = false)
		private List<TemperatureSensor> temperatureSensors;

		private GaugeService gaugeService;

		public MetricAdder(GaugeService gaugeService) {
			this.gaugeService = gaugeService;
		}

		@Scheduled(fixedRate = 1000)
		public void updateMetrics() {
			if (temperatureSensors != null) {
				for (TemperatureSensor sensor : temperatureSensors) {
					this.gaugeService.submit("iot.temperature." + sensor.getName(), sensor.getTemperature());
				}
			}
		}

	}

}

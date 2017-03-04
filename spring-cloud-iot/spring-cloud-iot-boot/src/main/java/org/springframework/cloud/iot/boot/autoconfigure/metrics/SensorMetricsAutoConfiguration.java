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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.iot.component.sensor.HumiditySensor;
import org.springframework.cloud.iot.component.sensor.TemperatureSensor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.Disposable;

/**
 * Boot auto-config which adds configurations for subscriptions with
 * known sensors and passes sensor values into boot's {@link GaugeService}.
 * Effectively this makes existing sensor values to be available via
 * boot's metric system.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
public class SensorMetricsAutoConfiguration {

	@Configuration
	@ConditionalOnProperty(prefix = "spring.cloud.iot.metrics.boot", name = "enabled", havingValue = "true", matchIfMissing = false)
	public static class MetricsConfiguration {
		private final GaugeService gaugeService;

		public MetricsConfiguration(ObjectProvider<GaugeService> gaugeServiceProvider) {
			this.gaugeService = gaugeServiceProvider.getIfAvailable();
		}

		@Bean
		public MetricDispatcher iotSensorMetricsMetricDispatcher() {
			return new MetricDispatcher(gaugeService);
		}
	}

	private static class MetricDispatcher {

		private final static Logger log = LoggerFactory.getLogger(MetricDispatcher.class);
		private final GaugeService gaugeService;
		private final List<Disposable> disposables = new ArrayList<>();
		private List<TemperatureSensor> temperatureSensors;
		private List<HumiditySensor> humiditySensors;

		public MetricDispatcher(GaugeService gaugeService) {
			this.gaugeService = gaugeService;
		}

		@Autowired(required = false)
		public void setTemperatureSensors(List<TemperatureSensor> temperatureSensors) {
			this.temperatureSensors = temperatureSensors;
		}

		@Autowired(required = false)
		public void setHumiditySensors(List<HumiditySensor> humiditySensors) {
			this.humiditySensors = humiditySensors;
		}

		@PostConstruct
		public void setup() {
			if (gaugeService == null) {
				log.info("No 'gaugeService' available, skipping sensor registrations");
				return;
			}
			if (temperatureSensors != null) {
				for (TemperatureSensor sensor : temperatureSensors) {
					String metricName = "iot.temperature." + sensor.getName();
					log.info("Subscribe metric dispatcher for {} as {}", sensor.getName(), metricName);
					Disposable disposable = sensor.getTemperature().asFlux().subscribe(t -> {
						log.debug("Dispatch metric {} {}", metricName, t);
						this.gaugeService.submit(metricName, t);
					});
					disposables.add(disposable);
				}
			}
			if (humiditySensors != null) {
				for (HumiditySensor sensor : humiditySensors) {
					String metricName = "iot.humidity." + sensor.getName();
					log.info("Subscribe metric dispatcher for {} as {}", sensor.getName(), metricName);
					Disposable disposable = sensor.getHumidity().asFlux().subscribe(t -> {
						log.debug("Dispatch metric {} {}", metricName, t);
						this.gaugeService.submit(metricName, t);
					});
					disposables.add(disposable);
				}
			}
		}

		@PreDestroy
		public void destroy() {
			for (Disposable d : disposables) {
				d.dispose();
			}
			disposables.clear();
		}
	}

}

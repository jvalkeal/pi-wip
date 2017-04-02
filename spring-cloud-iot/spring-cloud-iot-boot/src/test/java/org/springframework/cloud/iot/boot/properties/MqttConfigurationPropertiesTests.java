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
package org.springframework.cloud.iot.boot.properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

public class MqttConfigurationPropertiesTests {

	@Test
	public void testAllProperties() {
		SpringApplication app = new SpringApplication(TestConfiguration.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		ConfigurableApplicationContext context = app
				.run(new String[] { "--spring.config.name=MqttConfigurationPropertiesTests1" });
		MqttConfigurationProperties properties = context.getBean(MqttConfigurationProperties.class);
		assertThat(properties, notNullValue());

		assertThat(properties.getAddresses(), is("host1,host2"));
		assertThat(properties.determineAddresses(), containsInAnyOrder("tcp://host1:1883", "tcp://host2:1883"));
		assertThat(properties.getUsername(), is("john"));
		assertThat(properties.getPassword(), is("doe"));
		assertThat(properties.getConnectionTimeout(), is(1000));
		assertThat(properties.getSsl().isEnabled(), is(true));
		assertThat(properties.getSsl().getAlgorithm(), is("alg"));
		assertThat(properties.getSsl().getKeyStore(), is("kstore"));
		assertThat(properties.getSsl().getKeyStorePassword(), is("kstorep"));
		assertThat(properties.getSsl().getTrustStore(), is("tstore"));
		assertThat(properties.getSsl().getTrustStorePassword(), is("tstorep"));

		context.close();
	}

	@Test
	public void testUsernamePasswordSet() {
		SpringApplication app = new SpringApplication(TestConfiguration.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		ConfigurableApplicationContext context = app
				.run(new String[] { "--spring.config.name=MqttConfigurationPropertiesTests2" });
		MqttConfigurationProperties properties = context.getBean(MqttConfigurationProperties.class);
		assertThat(properties, notNullValue());

		assertThat(properties.determineAddresses(), containsInAnyOrder("tcp://localhost:1883"));
		assertThat(properties.getUsername(), is("john"));
		assertThat(properties.getPassword(), is("doe"));

		context.close();
	}

	@Test
	public void testUsernamePasswordSetInAddress() {
		SpringApplication app = new SpringApplication(TestConfiguration.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		ConfigurableApplicationContext context = app
				.run(new String[] { "--spring.config.name=MqttConfigurationPropertiesTests3" });
		MqttConfigurationProperties properties = context.getBean(MqttConfigurationProperties.class);
		assertThat(properties, notNullValue());

		assertThat(properties.determineAddresses(), containsInAnyOrder("tcp://host1:1883", "tcp://host2:1883"));
		assertThat(properties.getUsername(), nullValue());
		assertThat(properties.determineUsername(), is("user1"));
		assertThat(properties.getPassword(), nullValue());
		assertThat(properties.determinePassword(), is("pass1"));

		context.close();
	}

	@Configuration
	@EnableConfigurationProperties({ MqttConfigurationProperties.class })
	protected static class TestConfiguration {
	}

}

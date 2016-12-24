/*
 * Copyright 2016 the original author or authors.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.iot.Board;
import org.springframework.cloud.iot.boot.IotConfigurationProperties;
import org.springframework.cloud.iot.pi4j.RaspberryPiBoard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IotConfigurationProperties.class)
public class IotAutoConfiguration {

	@Autowired
	private IotConfigurationProperties iotConfigurationProperties;

	@Bean
	public Board iotBoard() {
		return new RaspberryPiBoard();
	}
}

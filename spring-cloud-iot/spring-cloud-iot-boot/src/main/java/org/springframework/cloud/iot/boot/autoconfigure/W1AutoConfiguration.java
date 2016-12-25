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

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.iot.boot.IotConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.pi4j.platform.Platform;

/**
 * Auto-configuration for W1 devices connected.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
@ConditionalOnClass(Platform.class)
@EnableConfigurationProperties(IotConfigurationProperties.class)
@EnableW1
public class W1AutoConfiguration {
}

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
package org.springframework.cloud.iot.stream.binder.xbee.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for coap binder itself.
 *
 * @author Janne Valkealahti
 *
 */
@ConfigurationProperties(prefix = "spring.cloud.stream.xbee.binder")
public class XBeeBinderConfigurationProperties {

	private Mode mode;

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public static enum Mode {
		OUTBOUND_GATEWAY,
		INBOUND_GATEWAY;
	}
}

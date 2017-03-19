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
package org.springframework.cloud.iot.xbee.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for XBee devices.
 *
 * @author Janne Valkealahti
 *
 */
@ConfigurationProperties(prefix = XBeeConfigurationProperties.NAMESPACE)
public class XBeeConfigurationProperties {

	public static final String NAMESPACE = "spring.cloud.iot.xbee";

	/** Serial port XBee is connected to */
	private String serialPort;

	/** Baud rate for serial communication */
	private Integer baudRate;

	/** XBee device receive timeout in milliseconds */
	private Integer receiveTimeout;

	public String getSerialPort() {
		return serialPort;
	}

	public void setSerialPort(String serialPort) {
		this.serialPort = serialPort;
	}

	public Integer getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(Integer baudRate) {
		this.baudRate = baudRate;
	}

	public Integer getReceiveTimeout() {
		return receiveTimeout;
	}

	public void setReceiveTimeout(Integer receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}
}

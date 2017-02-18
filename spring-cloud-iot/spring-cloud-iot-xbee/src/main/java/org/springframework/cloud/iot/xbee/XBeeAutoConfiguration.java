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
package org.springframework.cloud.iot.xbee;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;

@Configuration
@EnableConfigurationProperties(XBeeConfigurationProperties.class)
public class XBeeAutoConfiguration {

	@Bean
	@ConditionalOnProperty("spring.cloud.iot.xbee.serialPort")
	public XBeeDevice xbeeDevice(XBeeConfigurationProperties properties) throws XBeeException {
		SerialPortRxTx serialPortRxTx = new SerialPortRxTx(properties.getSerialPort(), 9600);
		XBeeDevice myDevice = new XBeeDevice(serialPortRxTx);
		myDevice.open();
		return myDevice;
	}
}

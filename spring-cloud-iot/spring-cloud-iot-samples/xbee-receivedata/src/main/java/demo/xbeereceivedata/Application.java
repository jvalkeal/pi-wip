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
package demo.xbeereceivedata;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.utils.HexUtils;

@SpringBootApplication
public class Application {

	private final static Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private XBeeDevice xbeeDevice;

	@PostConstruct
	public void setup() throws XBeeException {
		xbeeDevice.addDataListener(new MyDataReceiveListener());
		log.info("Waiting data");
	}

	private static class MyDataReceiveListener implements IDataReceiveListener {

		@Override
		public void dataReceived(XBeeMessage xbeeMessage) {
			log.info("From {} >> {} | {}", xbeeMessage.getDevice().get64BitAddress(),
					HexUtils.prettyHexString(HexUtils.byteArrayToHexString(xbeeMessage.getData())),
					new String(xbeeMessage.getData()));
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

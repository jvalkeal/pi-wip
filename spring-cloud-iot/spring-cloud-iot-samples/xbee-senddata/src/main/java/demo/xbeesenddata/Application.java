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
package demo.xbeesenddata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.utils.HexUtils;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private final static Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private XBeeDevice xbeeDevice;

	@Override
	public void run(String... args) throws Exception {
		XBeeNetwork xbeeNetwork = xbeeDevice.getNetwork();
		RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice("MASTER");

		if (remoteDevice != null) {
			log.info("device {}", remoteDevice.toString());
			for (int i = 0; i < 10; i++) {
				String data = "hello" + i;
				byte[] dataToSend = data.getBytes();
				log.info("Sending data to {} >> {} | {}... ", remoteDevice.get64BitAddress(),
						HexUtils.prettyHexString(HexUtils.byteArrayToHexString(dataToSend)),
						new String(dataToSend));
				xbeeDevice.sendData(remoteDevice, dataToSend);
				Thread.sleep(2000);
			}
		} else {
			log.error("device not found");
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

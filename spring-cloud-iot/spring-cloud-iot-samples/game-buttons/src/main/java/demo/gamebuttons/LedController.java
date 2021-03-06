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
package demo.gamebuttons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.iot.component.Led;
import org.springframework.cloud.iot.pi4j.support.Pi4jUtils;
import org.springframework.cloud.iot.support.IotUtils;
import org.springframework.stereotype.Component;

/**
 * {@link Component @Component} which is getting component scanned and
 * automatically created as a bean. Sole purpose of this class is as a group of
 * leds, flash those with a give configuration.
 *
 * @author Janne Valkealahti
 *
 */
@Component
public class LedController {

	@Autowired
	@Qualifier("GPIO_led1")
	private Led led1;

	@Autowired
	@Qualifier("GPIO_led2")
	private Led led2;

	@Autowired
	@Qualifier("GPIO_led3")
	private Led led3;

	@Autowired
	@Qualifier("GPIO_led4")
	private Led led4;

	/**
	 * Pulse given leds using a duration as milliseconds.
	 * <p>
	 * Leds combinations are given as bitmask where
	 * leds are identified by:
	 * <pre>
	 * 0x80=10000000
	 * 0x40=01000000
	 * 0x20=00100000
	 * 0x10=00010000
	 * </pre>
	 *
	 * @param leds the leds to pulse
	 * @param duration the duration as milliseconds
	 */
	public void pulse(int leds, int duration) {
		led1.setEnabled(!IotUtils.isBitSet(LedButton.LED1.getValue(), leds));
		led2.setEnabled(!IotUtils.isBitSet(LedButton.LED2.getValue(), leds));
		led3.setEnabled(!IotUtils.isBitSet(LedButton.LED3.getValue(), leds));
		led4.setEnabled(!IotUtils.isBitSet(LedButton.LED4.getValue(), leds));

		Pi4jUtils.delay(duration);
		led1.setEnabled(true);
		led2.setEnabled(true);
		led3.setEnabled(true);
		led4.setEnabled(true);
	}
}

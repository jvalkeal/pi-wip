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
package org.springframework.cloud.iot.component.sensor;

/**
 * {@code HumiditySensor} is a {@link Sensor}
 * returning a humidity. Humidities are values
 * ranging from 0 to 100 inclusive.
 *
 * @author Janne Valkealahti
 *
 */
public interface HumiditySensor extends Sensor {

	/**
	 * Gets the humidity.
	 *
	 * @return the humidity
	 */
	Humidity getHumidity();
}

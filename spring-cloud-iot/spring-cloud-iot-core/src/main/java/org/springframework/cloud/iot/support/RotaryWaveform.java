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
package org.springframework.cloud.iot.support;

/**
 * Utility class calculating values out from a two waveforms.
 *
 * W1   +----+    +----+             +----+    +----+
 *      |    |    |    |             |    |    |    |
 *    --+    +----+    +-- ....... --+    +----+    +--
 *
 * W2     +----+    +----+         +----+    +----+
 *        |    |    |    |         |    |    |    |
 *    ----+    +----+    +-- ... --+    +----+    +--
 *
 * @author Janne Valkealahti
 */
public class RotaryWaveform {

	private final int incrementCount;
	private volatile int counter;
	private volatile boolean w1;
	private volatile boolean w2;

	// < 0 ccw, 0 not known, > 0 cw
	private volatile int direction;

	/**
	 * Instantiates a new rotary waveform with number of
	 * increment steps encoder supports.
	 *
	 * @param incrementCount the increment count
	 */
	public RotaryWaveform(int incrementCount) {
		this.incrementCount = incrementCount;
	}

	public void pulseW1Start() {
		w1 = true;
		if (direction >= 0) {
			direction = 1;
			counter++;
		} else if (!w2) {
			counter++;
			direction = 1;
		}
	}

	public void pulseW1Stop() {
		w1 = false;
	}

	public void pulseW2Start() {
		w2 = true;
		if (direction <= 0) {
			counter--;
			direction = -1;
		} else if (!w1) {
			counter--;
			direction = -1;
		}
	}

	public void pulseW2Stop() {
		w2 = false;
	}

	public int getCounter() {
		return counter;
	}

	public void reset() {
		counter = 0;
	}
}

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeedGame {

	private static final Logger log = LoggerFactory.getLogger(SpeedGame.class);
	private final int[] queue;
	private int tail = -1;
	private int head = 0;

	public SpeedGame(int[] queue) {
		this.queue = queue;
	}

	public boolean led() {
		log.info("led {}", this);
		if (head >= queue.length) {
			return false;
		} else {
			head++;
			return true;
		}
	}

	public boolean button(int button) {
		log.info("button {}", this);
		return queue[++tail] != button;
	}

	@Override
	public String toString() {
		return "SpeedGame [tail=" + tail + ", head=" + head + "]";
	}

}

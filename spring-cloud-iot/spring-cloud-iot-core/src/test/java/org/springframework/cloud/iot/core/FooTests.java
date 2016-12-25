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
package org.springframework.cloud.iot.core;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FooTests {

	private final static Logger log = LoggerFactory.getLogger(FooTests.class);


	@Test
	public void testFoo() {

		int[] colors = new int[] { 0xFF00, 0x00FF, 0x0FF0, 0xF00F };
		for (int color : colors) {
			int R_val = color  >> 8;
			int G_val = color & 0x00FF;

			int R_val2 = map(R_val, 0, 255, 0, 100);
			int G_val2 = map(G_val, 0, 255, 0, 100);

			log.info("R_val {} {}", R_val, R_val2);
			log.info("G_val {} {}", G_val, G_val2);
		}

	}

	private static int map(int x, int in_min, int in_max, int out_min, int out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
//	def map(x, in_min, in_max, out_min, out_max):
//		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min
}

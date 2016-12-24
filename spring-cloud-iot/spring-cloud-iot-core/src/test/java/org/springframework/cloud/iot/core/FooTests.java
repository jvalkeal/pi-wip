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

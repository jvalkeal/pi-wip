package org.springframework.coap;

import org.junit.Test;

public class FooTests {

	@Test
	public void testFoo() {
		setIntegerValue(0);
	}

	private byte[] value;

//	public void setIntegerValue(int val) {
//		int length = 0;
//		for (int i=0;i<4;i++)
//			if (val >= 1<<(i*8) || val < 0) length++;
//			else break;
//		value = new byte[length];
//		for (int i=0;i<length;i++)
//			value[length - i - 1] = (byte) (val >> i*8);
//	}


	public void setIntegerValue(int val) {
		int length = 0;
		for (int i=0;i<4;i++) {
			int x = 1<<(i*8);
			if (val >= 1<<(i*8) || val < 0) {
				length++;
			}
			else {
				break;
			}
		}
		value = new byte[length];
		for (int i=0;i<length;i++)
			value[length - i - 1] = (byte) (val >> i*8);
	}

}

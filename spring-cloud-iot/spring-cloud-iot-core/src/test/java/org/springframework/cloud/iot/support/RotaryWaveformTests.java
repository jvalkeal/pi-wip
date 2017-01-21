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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for {@link RotaryWaveform}.
 *
 * @author Janne Valkealahti
 *
 */
public class RotaryWaveformTests {

	@Test
	public void testRotateCW() {
		RotaryWaveform rw = new RotaryWaveform(20);
		assertThat(rw.getCounter(), is(0));

		rw.pulseW1Start();
		assertThat(rw.getCounter(), is(1));
		rw.pulseW2Start();
		assertThat(rw.getCounter(), is(1));
		rw.pulseW1Stop();
		assertThat(rw.getCounter(), is(1));
		rw.pulseW2Stop();
		assertThat(rw.getCounter(), is(1));

		rw.pulseW1Start();
		assertThat(rw.getCounter(), is(2));
		rw.pulseW2Start();
		assertThat(rw.getCounter(), is(2));
		rw.pulseW1Stop();
		assertThat(rw.getCounter(), is(2));
		rw.pulseW2Stop();
		assertThat(rw.getCounter(), is(2));

	}

	@Test
	public void testRotateCCW() {
		RotaryWaveform rw = new RotaryWaveform(20);
		assertThat(rw.getCounter(), is(0));

		rw.pulseW2Start();
		assertThat(rw.getCounter(), is(-1));
		rw.pulseW1Start();
		assertThat(rw.getCounter(), is(-1));
		rw.pulseW2Stop();
		assertThat(rw.getCounter(), is(-1));
		rw.pulseW1Stop();
		assertThat(rw.getCounter(), is(-1));

		rw.pulseW2Start();
		assertThat(rw.getCounter(), is(-2));
		rw.pulseW1Start();
		assertThat(rw.getCounter(), is(-2));
		rw.pulseW2Stop();
		assertThat(rw.getCounter(), is(-2));
		rw.pulseW1Stop();
		assertThat(rw.getCounter(), is(-2));
	}

	@Test
	public void testRotateCWThenCCW() {
		RotaryWaveform rw = new RotaryWaveform(20);
		assertThat(rw.getCounter(), is(0));

		rw.pulseW1Start();
		assertThat(rw.getCounter(), is(1));
		rw.pulseW2Start();
		assertThat(rw.getCounter(), is(1));
		rw.pulseW1Stop();
		assertThat(rw.getCounter(), is(1));
		rw.pulseW2Stop();
		assertThat(rw.getCounter(), is(1));

		rw.pulseW2Start();
		assertThat(rw.getCounter(), is(0));
		rw.pulseW1Start();
		assertThat(rw.getCounter(), is(0));
		rw.pulseW2Stop();
		assertThat(rw.getCounter(), is(0));
		rw.pulseW1Stop();
		assertThat(rw.getCounter(), is(0));

		rw.pulseW2Start();
		assertThat(rw.getCounter(), is(-1));
		rw.pulseW1Start();
		assertThat(rw.getCounter(), is(-1));
		rw.pulseW2Stop();
		assertThat(rw.getCounter(), is(-1));
		rw.pulseW1Stop();
		assertThat(rw.getCounter(), is(-1));
	}

	@Test
	public void testRotateCCWThenCW() {
		RotaryWaveform rw = new RotaryWaveform(20);
		assertThat(rw.getCounter(), is(0));

		rw.pulseW2Start();
		assertThat(rw.getCounter(), is(-1));
		rw.pulseW1Start();
		assertThat(rw.getCounter(), is(-1));
		rw.pulseW2Stop();
		assertThat(rw.getCounter(), is(-1));
		rw.pulseW1Stop();
		assertThat(rw.getCounter(), is(-1));

		rw.pulseW1Start();
		assertThat(rw.getCounter(), is(0));
		rw.pulseW2Start();
		assertThat(rw.getCounter(), is(0));
		rw.pulseW1Stop();
		assertThat(rw.getCounter(), is(0));
		rw.pulseW2Stop();
		assertThat(rw.getCounter(), is(0));

		rw.pulseW1Start();
		assertThat(rw.getCounter(), is(1));
		rw.pulseW2Start();
		assertThat(rw.getCounter(), is(1));
		rw.pulseW1Stop();
		assertThat(rw.getCounter(), is(1));
		rw.pulseW2Stop();
		assertThat(rw.getCounter(), is(1));
	}

}

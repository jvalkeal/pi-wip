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
package org.springframework.cloud.iot.pi4j;

import org.springframework.cloud.iot.component.IncrementalRotary;
import org.springframework.cloud.iot.listener.CompositeRotaryListener;
import org.springframework.cloud.iot.listener.RotaryListener;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;
import org.springframework.cloud.iot.support.RotaryWaveform;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Pi4jIncrementalRotary extends LifecycleObjectSupport implements IncrementalRotary {

	private final RotaryWaveform waveform;
	private final GpioPinDigitalInput left;
	private final GpioPinDigitalInput right;
	private CompositeRotaryListener rotaryListener;

	public Pi4jIncrementalRotary(Integer incrementalSteps, GpioPinDigitalInput left, GpioPinDigitalInput right) {
		this.waveform = new RotaryWaveform(incrementalSteps);
		this.left = left;
		this.right = right;
	}

	@Override
	protected void onInit() throws Exception {
		left.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if (event.getEdge() == PinEdge.RISING) {
					waveform.pulseW1Start();
				} else {
					waveform.pulseW1Stop();
					notifyRotaryListener();
				}
			}
		});
		right.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if (event.getEdge() == PinEdge.RISING) {
					waveform.pulseW2Start();
				} else {
					waveform.pulseW2Stop();
					notifyRotaryListener();
				}
			}
		});
	}

	@Override
	public int getCounter() {
		return waveform.getCounter();
	}

	@Override
	public void reset() {
		waveform.reset();
	}

	@Override
	public void addRotaryListener(RotaryListener listener) {
		synchronized (this) {
			if (rotaryListener == null) {
				rotaryListener = new CompositeRotaryListener();
			}
			rotaryListener.register(listener);
		}
	}

	@Override
	public void removeRotaryListener(RotaryListener listener) {
		synchronized (this) {
			if (rotaryListener != null) {
				rotaryListener.unregister(listener);
			}
		}
	}

	private void notifyRotaryListener() {
		if (rotaryListener != null) {
			rotaryListener.onRotate();
		}
	}

}

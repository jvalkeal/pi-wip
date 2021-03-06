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
package org.springframework.cloud.iot.boot.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.iot.properties.ButtonProperties;
import org.springframework.cloud.iot.properties.BuzzerProperties;
import org.springframework.cloud.iot.properties.DimmedLedProperties;
import org.springframework.cloud.iot.properties.HumidityProperties;
import org.springframework.cloud.iot.properties.IncrementalRotaryProperties;
import org.springframework.cloud.iot.properties.JoystickProperties;
import org.springframework.cloud.iot.properties.LcdProperties;
import org.springframework.cloud.iot.properties.LedProperties;
import org.springframework.cloud.iot.properties.PotentiometerProperties;
import org.springframework.cloud.iot.properties.RelayProperties;
import org.springframework.cloud.iot.properties.ShiftRegisterProperties;
import org.springframework.cloud.iot.properties.TermistorProperties;

/**
 * Properties for "spring.cloud.iot".
 *
 * @author Janne Valkealahti
 *
 */
@ConfigurationProperties(prefix = IotConfigurationProperties.NAMESPACE)
public class IotConfigurationProperties {

	public static final String NAMESPACE = "spring.cloud.iot";

	private Map<String, ComponentType> components = new HashMap<>();

	public Map<String, ComponentType> getComponents() {
		return components;
	}

	public void setComponents(Map<String, ComponentType> components) {
		this.components = components;
	}

	public static class ComponentType {

		private IncrementalRotaryProperties incrementalRotary;
		private ButtonProperties button;
		private DimmedLedProperties dimmedLed;
		private LedProperties led;
		private RelayProperties relay;
		private ShiftRegisterProperties shiftRegister;
		private PotentiometerProperties potentiometer;
		private LcdProperties lcd;
		private TermistorProperties termistor;
		private HumidityProperties humidity;
		private JoystickProperties joystick;
		private BuzzerProperties buzzer;

		public IncrementalRotaryProperties getIncrementalRotary() {
			return incrementalRotary;
		}

		public void setIncrementalRotary(IncrementalRotaryProperties incrementalRotary) {
			this.incrementalRotary = incrementalRotary;
		}

		public ButtonProperties getButton() {
			return button;
		}

		public void setButton(ButtonProperties button) {
			this.button = button;
		}

		public DimmedLedProperties getDimmedLed() {
			return dimmedLed;
		}

		public void setDimmedLed(DimmedLedProperties dimmedLed) {
			this.dimmedLed = dimmedLed;
		}

		public LedProperties getLed() {
			return led;
		}

		public void setLed(LedProperties led) {
			this.led = led;
		}

		public RelayProperties getRelay() {
			return relay;
		}

		public void setRelay(RelayProperties relay) {
			this.relay = relay;
		}

		public ShiftRegisterProperties getShiftRegister() {
			return shiftRegister;
		}

		public void setShiftRegister(ShiftRegisterProperties shiftRegister) {
			this.shiftRegister = shiftRegister;
		}

		public PotentiometerProperties getPotentiometer() {
			return potentiometer;
		}

		public void setPotentiometer(PotentiometerProperties potentiometer) {
			this.potentiometer = potentiometer;
		}

		public LcdProperties getLcd() {
			return lcd;
		}

		public void setLcd(LcdProperties lcd) {
			this.lcd = lcd;
		}

		public TermistorProperties getTermistor() {
			return termistor;
		}

		public void setTermistor(TermistorProperties termistor) {
			this.termistor = termistor;
		}

		public HumidityProperties getHumidity() {
			return humidity;
		}

		public void setHumidity(HumidityProperties humidity) {
			this.humidity = humidity;
		}

		public JoystickProperties getJoystick() {
			return joystick;
		}

		public void setJoystick(JoystickProperties joystick) {
			this.joystick = joystick;
		}

		public BuzzerProperties getBuzzer() {
			return buzzer;
		}

		public void setBuzzer(BuzzerProperties buzzer) {
			this.buzzer = buzzer;
		}
	}

	public static enum NumberingScheme {
		BROADCOM, WIRINGPI;
	}

}

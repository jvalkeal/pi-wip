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
package org.springframework.cloud.iot.boot;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = I2cConfigurationProperties.NAMESPACE)
public class I2cConfigurationProperties {

	public static final String NAMESPACE = "spring.cloud.iot.i2c";

	private Map<Integer, AddressType> addresses = new HashMap<Integer, AddressType>();

	public Map<Integer, AddressType> getAddresses() {
		return addresses;
	}
	public void setAddresses(Map<Integer, AddressType> addresses) {
		this.addresses = addresses;
	}

	public static class AddressType {
		private Integer bus;
		private TermistorType termistor;
		private LcdType lcd;

		public TermistorType getTermistor() {
			return termistor;
		}
		public void setTermistor(TermistorType termistor) {
			this.termistor = termistor;
		}
		public LcdType getLcd() {
			return lcd;
		}
		public void setLcd(LcdType lcd) {
			this.lcd = lcd;
		}
		public Integer getBus() {
			return bus;
		}
		public void setBus(Integer bus) {
			this.bus = bus;
		}
	}

	public static class TermistorType {
		private Double supplyVoltage;
		private Integer resistance;
		private Double referenceTemp;
		private Integer dacBits;
		private Double beta;

		public Double getSupplyVoltage() {
			return supplyVoltage;
		}
		public void setSupplyVoltage(Double supplyVoltage) {
			this.supplyVoltage = supplyVoltage;
		}
		public Integer getResistance() {
			return resistance;
		}
		public void setResistance(Integer resistance) {
			this.resistance = resistance;
		}
		public Double getReferenceTemp() {
			return referenceTemp;
		}
		public void setReferenceTemp(Double referenceTemp) {
			this.referenceTemp = referenceTemp;
		}
		public Integer getDacBits() {
			return dacBits;
		}
		public void setDacBits(Integer dacBits) {
			this.dacBits = dacBits;
		}
		public Double getBeta() {
			return beta;
		}
		public void setBeta(Double beta) {
			this.beta = beta;
		}
	}

	public static class LcdType {
		private int rows = 2;
		private int colums = 16;
		private boolean clearTextOnExit = true;
		public int getRows() {
			return rows;
		}
		public void setRows(int rows) {
			this.rows = rows;
		}
		public int getColums() {
			return colums;
		}
		public void setColums(int colums) {
			this.colums = colums;
		}
		public boolean isClearTextOnExit() {
			return clearTextOnExit;
		}
		public void setClearTextOnExit(boolean clearTextOnExit) {
			this.clearTextOnExit = clearTextOnExit;
		}
	}

	public static enum ComponentType {
		termistor;
	}

}

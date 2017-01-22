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
package org.springframework.cloud.iot.properties;

public class TermistorProperties extends ComponentProperties {

	private Double supplyVoltage;
	private Integer resistance;
	private Double referenceTemp;
	private Integer dacBits;
	private Double beta;
	private I2CProperties i2c;

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

	public I2CProperties getI2c() {
		return i2c;
	}

	public void setI2c(I2CProperties i2c) {
		this.i2c = i2c;
	}
}

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

public class HD44780Properties {

	private Integer rsPin;
	private Integer ePin;
	private Integer d4Pin;
	private Integer d5Pin;
	private Integer d6Pin;
	private Integer d7Pin;

	public Integer getRsPin() {
		return rsPin;
	}
	public void setRsPin(Integer rsPin) {
		this.rsPin = rsPin;
	}
	public Integer getEPin() {
		return ePin;
	}
	public void setEPin(Integer ePin) {
		this.ePin = ePin;
	}
	public Integer getD4Pin() {
		return d4Pin;
	}
	public void setD4Pin(Integer d4Pin) {
		this.d4Pin = d4Pin;
	}
	public Integer getD5Pin() {
		return d5Pin;
	}
	public void setD5Pin(Integer d5Pin) {
		this.d5Pin = d5Pin;
	}
	public Integer getD6Pin() {
		return d6Pin;
	}
	public void setD6Pin(Integer d6Pin) {
		this.d6Pin = d6Pin;
	}
	public Integer getD7Pin() {
		return d7Pin;
	}
	public void setD7Pin(Integer d7Pin) {
		this.d7Pin = d7Pin;
	}
}

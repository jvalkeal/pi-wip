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

import java.util.HashMap;
import java.util.Map;

/**
 * Properties for  {@code LCD} displays.
 *
 * @author Janne Valkealahti
 */
public class LcdProperties extends ComponentProperties {

	/** Number of rows in this display */
	private Integer rows;

	/** Number of colums in this display */
	private Integer columns;

	/** Clear display when component is closed */
	private Boolean clearOnExit = true;

	/** I2C configuration */
	private I2CProperties i2c;

	private HD44780Properties hd44780;

	private PCF8574Properties pcf8574;

	private Map<String, LayoutProperties> layout = new HashMap<>();

	/**
	 * Gets the rows.
	 *
	 * @return the rows
	 */
	public Integer getRows() {
		return rows;
	}

	/**
	 * Sets the rows.
	 *
	 * @param rows the new rows
	 */
	public void setRows(Integer rows) {
		this.rows = rows;
	}

	/**
	 * Gets the columns.
	 *
	 * @return the columns
	 */
	public Integer getColumns() {
		return columns;
	}

	/**
	 * Sets the columns.
	 *
	 * @param columns the new columns
	 */
	public void setColumns(Integer columns) {
		this.columns = columns;
	}

	/**
	 * Gets the i2c.
	 *
	 * @return the i2c
	 */
	public I2CProperties getI2c() {
		return i2c;
	}

	/**
	 * Sets the i2c.
	 *
	 * @param i2c the new i2c
	 */
	public void setI2c(I2CProperties i2c) {
		this.i2c = i2c;
	}

	public HD44780Properties getHd44780() {
		return hd44780;
	}

	public void setHd44780(HD44780Properties hd44780) {
		this.hd44780 = hd44780;
	}

	public PCF8574Properties getPcf8574() {
		return pcf8574;
	}

	public void setPcf8574(PCF8574Properties pcf8574) {
		this.pcf8574 = pcf8574;
	}

	/**
	 * Gets the clear on exit.
	 *
	 * @return the clear on exit
	 */
	public Boolean getClearOnExit() {
		return clearOnExit;
	}

	/**
	 * Sets the clear on exit.
	 *
	 * @param clearOnExit the new clear on exit
	 */
	public void setClearOnExit(Boolean clearOnExit) {
		this.clearOnExit = clearOnExit;
	}

	public Map<String, LayoutProperties> getLayout() {
		return layout;
	}

	public void setLayout(Map<String, LayoutProperties> layout) {
		this.layout = layout;
	}

}

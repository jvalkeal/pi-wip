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
package org.springframework.cloud.iot.component.display;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.properties.LayoutProperties;
import org.springframework.cloud.iot.properties.LayoutProperties.AreaType;

/**
 * The Class LayoutManager.
 */
public class LayoutManager {

	/** The Constant log. */
	private final static Logger log = LoggerFactory.getLogger(LayoutManager.class);

	/** The max width. */
	private int maxWidth = 0;

	/** The max height. */
	private int maxHeight = 0;

	/** The segments. */
	private Map<String, String> segments = new HashMap<>();

	/** The layout. */
	private Map<String, LayoutProperties> layout;

	/**
	 * Instantiates a new layout manager.
	 *
	 * @param layout the layout
	 */
	public LayoutManager(Map<String, LayoutProperties> layout) {
		this.layout = layout;
		for (Entry<String, LayoutProperties> entry : layout.entrySet()) {
			AreaType area = entry.getValue().getArea();
			if ((area.getX() + area.getWidth()) > maxWidth) {
				maxWidth = area.getX() + area.getWidth();
			}
			if ((area.getY() + area.getHeight()) > maxHeight) {
				maxHeight = area.getY() + area.getHeight();
			}
		}
	}

	/**
	 * Sets the content.
	 *
	 * @param segmentName the segment name
	 * @param content the content
	 */
	public void setContent(String segmentName, String content) {
		segments.put(segmentName, content);
	}

	/**
	 * Layout content.
	 *
	 * @return the character[][]
	 */
	public Character[][] layoutContent() {
		Character[][] data = new Character[maxHeight][maxWidth];

		for (Entry<String, LayoutProperties> entry : layout.entrySet()) {
			int x = entry.getValue().getArea().getX();
			int y = entry.getValue().getArea().getY();
			String content = segments.get(entry.getKey());
			if (content == null) {
				content = entry.getValue().getInitial();
			}
			if (content == null) {
				content = "";
			}
			for (int i = 0; i < content.length(); i++) {
				data[y][x+i] = content.charAt(i);
			}
		}

		return data;
	}

}

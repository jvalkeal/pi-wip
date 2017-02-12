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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.cloud.iot.properties.LayoutProperties;
import org.springframework.cloud.iot.properties.LayoutProperties.AreaType;

public class LayoutManagerTests {

	@Test
	public void testOneSegmentDefault() {
		Map<String, LayoutProperties> layout = new HashMap<>();
		LayoutProperties p1 = new LayoutProperties();
		AreaType a1 = new AreaType();
		a1.setX(0);
		a1.setY(0);
		a1.setWidth(5);
		a1.setHeight(1);
		p1.setArea(a1);
		p1.setInitial("foo");
		layout.put("s1", p1);
		LayoutManager manager = new LayoutManager(layout);
		Character[][] layoutContent = manager.layoutContent();
		assertThat(layoutContent, notNullValue());
	}

	@Test
	public void testOneSegmentSetContent() {
		Map<String, LayoutProperties> layout = new HashMap<>();
		LayoutProperties p1 = new LayoutProperties();
		AreaType a1 = new AreaType();
		a1.setX(0);
		a1.setY(0);
		a1.setWidth(5);
		a1.setHeight(1);
		p1.setArea(a1);
		p1.setInitial("foo");
		layout.put("s1", p1);
		LayoutManager manager = new LayoutManager(layout);
		manager.setContent("s1", "bar");
		Character[][] layoutContent = manager.layoutContent();
		assertThat(layoutContent, notNullValue());
	}

}

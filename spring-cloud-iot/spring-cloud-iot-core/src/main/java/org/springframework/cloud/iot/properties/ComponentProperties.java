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

import java.util.List;
import java.util.Map;

/**
 * Generic component properties.
 *
 * @author Janne Valkealahti
 *
 */
public abstract class ComponentProperties {

	/** Flag if component is enabled */
	private boolean enabled = true;

	/** Tags for the component */
	private List<String> tags;

	/** Properties for the component */
	private Map<String, Object> properties;

	/**
	 * Checks if component is enabled.
	 *
	 * @return true, if is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the component enabled flag.
	 *
	 * @param enabled the new enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets the component tags.
	 *
	 * @return the component tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * Sets the component tags.
	 *
	 * @param tags the new component tags
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * Sets the properties.
	 *
	 * @param properties the properties
	 */
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
}

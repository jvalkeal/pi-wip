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
package org.springframework.cloud.iot.component;

import java.util.Collection;

/**
 * {@code Taggable} is an interface for a component aware of a collection of
 * {@code tags}. Instead of a component having a single unique identifier it can
 * be also identified by a collection of tag identifiers.
 *
 * @author Janne Valkealahti
 *
 */
public interface IotTags {

	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	Collection<String> getTags();

	/**
	 * Sets the tags.
	 *
	 * @param tags the new tags
	 */
	void setTags(Collection<String> tags);
}

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
package org.springframework.cloud.iot.event;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.ApplicationEvent;

/**
 * Base {@link ApplicationEvent} class for iot based events. All custom event
 * classes should be derived from this class.
 *
 * @author Janne Valkealahti
 *
 */
public abstract class IotEvent extends ApplicationEvent {

	private static final long serialVersionUID = -4596603783239694082L;
	protected final Collection<String> tags = new ArrayList<>();

	/**
	 * Instantiates a new {@code IotEvent}.
	 *
	 * @param source the component that published the event (never {@code null})
	 */
	public IotEvent(Object source) {
		super(source);
	}

	/**
	 * Gets the {@code eventId} which uniquely identify different
	 * {@link IotEvent} types from each others.
	 *
	 * @return the event id
	 */
	public abstract String getEventId();

	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	public Collection<String> getTags() {
		return tags;
	}

	@Override
	public String toString() {
		return "IotEvent [tags=" + tags + ", source=" + source + "]";
	}
}

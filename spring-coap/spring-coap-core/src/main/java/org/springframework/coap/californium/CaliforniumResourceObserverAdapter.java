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
package org.springframework.coap.californium;

import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.core.server.resources.ResourceObserver;

/**
 * Simple adapter for {@link ResourceObserver} implementing
 * stub methods for it.
 *
 * @author Janne Valkealahti
 *
 */
public class CaliforniumResourceObserverAdapter implements ResourceObserver {

	@Override
	public void changedName(String old) {
	}

	@Override
	public void changedPath(String old) {
	}

	@Override
	public void addedChild(Resource child) {
	}

	@Override
	public void removedChild(Resource child) {
	}

	@Override
	public void addedObserveRelation(ObserveRelation relation) {
	}

	@Override
	public void removedObserveRelation(ObserveRelation relation) {
	}
}

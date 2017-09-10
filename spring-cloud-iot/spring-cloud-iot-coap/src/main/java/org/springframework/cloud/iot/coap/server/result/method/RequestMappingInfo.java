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
package org.springframework.cloud.iot.coap.server.result.method;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.iot.coap.server.RequestCondition;
import org.springframework.cloud.iot.coap.server.ServerCoapExchange;

public class RequestMappingInfo implements RequestCondition<RequestMappingInfo>{

	private List<String> paths;

	public RequestMappingInfo(List<String> paths) {
		this.paths = paths;
	}

	@Override
	public RequestMappingInfo combine(RequestMappingInfo other) {
		ArrayList<String> newPaths = new ArrayList<>();
		for (String path1 : other.getPaths()) {
			for (String path2 : getPaths()) {
				newPaths.add(path2 + path1);
			}
		}
		return new RequestMappingInfo(newPaths);
	}

	@Override
	public RequestMappingInfo getMatchingCondition(ServerCoapExchange exchange) {
		return null;
	}

	@Override
	public int compareTo(RequestMappingInfo other, ServerCoapExchange exchange) {
		return 0;
	}

	public List<String> getPaths() {
		return paths;
	}
}

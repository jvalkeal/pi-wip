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
package org.springframework.cloud.iot.gateway.client;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Bindable interface with one output channel.
 *
 * @author Janne Valkealahti
 *
 */
public interface GatewayClient {

	String OUTPUT = "iotGatewayClient";
	String OUTPUT_REQUEST = "iotGatewayClient" + "Request";
	String OUTPUT_REPLY = "iotGatewayClient" + "Reply";
	String OUTPUT_RESPONSE = "iotGatewayClient" + "Response";

	@Output(GatewayClient.OUTPUT)
	MessageChannel gatewayClient();
}

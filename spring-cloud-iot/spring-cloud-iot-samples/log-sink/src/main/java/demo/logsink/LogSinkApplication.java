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
package demo.logsink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.app.log.sink.LogSinkConfiguration;
import org.springframework.cloud.stream.app.log.sink.LogSinkProperties;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.handler.LoggingHandler;

@SpringBootApplication
@EnableBinding(Source.class)
@Import(LogSinkConfiguration.class)
public class LogSinkApplication {

	@Autowired
	private LogSinkProperties properties;

	@Bean
	@ServiceActivator(inputChannel = Sink.INPUT)
	public LoggingHandler logSinkHandler() {
		LoggingHandler loggingHandler = new LoggingHandler(this.properties.getLevel().name());
		loggingHandler.setLogExpressionString(this.properties.getExpression());
		loggingHandler.setLoggerName(this.properties.getName());
		return loggingHandler;
	}

	public static void main(String[] args) {
		SpringApplication.run(LogSinkApplication.class, args);
	}
}

package com.example;

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

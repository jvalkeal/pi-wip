package com.example;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.app.annotation.PollableSource;
import org.springframework.cloud.stream.app.trigger.TriggerConfiguration;
import org.springframework.cloud.stream.app.trigger.TriggerProperties;
import org.springframework.cloud.stream.app.trigger.TriggerPropertiesMaxMessagesDefaultOne;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableBinding(Source.class)
@Import({TriggerConfiguration.class, TriggerPropertiesMaxMessagesDefaultOne.class})
public class TimeSourceApplication {

	@Autowired
	private TriggerProperties triggerProperties;

	@PollableSource
	public String publishTime() {
		return new SimpleDateFormat(this.triggerProperties.getDateFormat()).format(new Date());
	}

	public static void main(String[] args) {
		SpringApplication.run(TimeSourceApplication.class, args);
	}
}

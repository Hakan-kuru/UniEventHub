package com.hakankuru.EventTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EventTimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventTimeApplication.class, args);
	}

}

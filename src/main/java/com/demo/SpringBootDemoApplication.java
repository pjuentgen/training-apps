package com.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootDemoApplication {

	private static final Logger logger = LoggerFactory.getLogger(SpringBootDemoApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Spring Boot Demo Application...");
		SpringApplication.run(SpringBootDemoApplication.class, args);
		logger.info("Spring Boot Demo Application started successfully!");
	}

}
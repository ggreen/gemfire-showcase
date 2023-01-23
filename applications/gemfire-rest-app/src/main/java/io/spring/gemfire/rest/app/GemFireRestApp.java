package io.spring.gemfire.rest.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GemFireRestApp {

	public static void main(String[] args) {
		System.setProperty("PDX_READ_SERIALIZED","true");
		SpringApplication.run(GemFireRestApp.class, args);
	}
}

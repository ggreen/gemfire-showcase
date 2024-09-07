package io.spring.gemfire.rest.app;

import com.vmware.data.services.gemfire.client.GemFireClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GemFireRestApp {

	public static void main(String[] args) {
		System.setProperty("PDX_READ_SERIALIZED","true");
		GemFireClient.connect();

		SpringApplication.run(GemFireRestApp.class, args);
	}
}

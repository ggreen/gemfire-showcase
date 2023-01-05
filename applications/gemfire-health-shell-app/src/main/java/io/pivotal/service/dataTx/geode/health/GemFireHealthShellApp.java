package io.pivotal.service.dataTx.geode.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GemFireHealthShellApp {
    public static void main(String[] args) {
        SpringApplication.run(GemFireHealthShellApp.class);
    }
}

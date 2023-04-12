package com.vmware.data.solutions.gemfire;

import com.vmware.data.services.gemfire.client.GemFireClient;
import com.vmware.data.services.gemfire.serialization.PDX;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.pdx.PdxInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Slf4j
public class GemFireConfig {
    @Value("${regionName}")
    private String regionName;

    @Value("${spring.data.gemfire.pool.locators:localhost[10334]}")
    private String locators;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.data.gemfire.security.username:admin}")
    private String userName;

    @Value("${spring.data.gemfire.security.password:admin}")
    private String password;

    @Bean
    PDX pdx()  {
        return new PDX();
    }

    @Bean
    GemFireClient gemfireClient()
    {
        return GemFireClient.builder().locators(locators).clientName(applicationName)
                .userName(userName)
                .password(password.toCharArray())
                .build();
    }

    @Bean
    Map<String, PdxInstance> createRegion(GemFireClient gemfire) {

        log.info("Getting region: "+regionName);
        return gemfire.getRegion(regionName);
    }

}

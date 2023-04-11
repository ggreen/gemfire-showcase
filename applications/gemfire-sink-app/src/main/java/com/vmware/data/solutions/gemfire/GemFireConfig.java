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


    @Bean
    PDX pdx()  {
        return new PDX();
    }

    @Bean
    Map<String, PdxInstance> createRegion() {

        log.info("Getting region: "+regionName);
        return GemFireClient.connect().getRegion(regionName);
    }

}

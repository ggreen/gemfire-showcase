package io.spring.gemfire.perftest;

import com.vmware.data.services.gemfire.client.GemFireClient;
import org.apache.geode.cache.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GemFireConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${regionName}")
    private String regionName;

    @Value("${spring.data.gemfire.pool.locators}")
    private String locators;

    @Bean
    GemFireClient gemfire()
    {
        return GemFireClient.builder()
                .locators(locators)
                .clientName(applicationName)
                .build();
    }

    @Bean("perfTestRegion")
    Region<Object,Object> getRegion(GemFireClient gemFire){
        return gemFire.getRegion(regionName);
    }

}

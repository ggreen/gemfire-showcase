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

    @Value("${spring.data.gemfire.security.username:admin}")
    private String userName;

    @Value("${spring.data.gemfire.security.password:admin}")
    private String password;

    @Bean
    GemFireClient gemfire()
    {
        return GemFireClient.builder()
                .locators(locators)
                .userName(userName)
                .password(password.toCharArray())
                .clientName(applicationName)
                .build();
    }

    @Bean("perfTestRegion")
    Region<Object,Object> getRegion(GemFireClient gemFire){
        return gemFire.getRegion(regionName);
    }

}

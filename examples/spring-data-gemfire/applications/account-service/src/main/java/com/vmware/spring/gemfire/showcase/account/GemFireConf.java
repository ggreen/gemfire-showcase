package com.vmware.spring.gemfire.showcase.account;

import com.vmware.spring.gemfire.showcase.account.entity.Account;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.GemFireCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableSecurity;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

@ClientCacheApplication
@EnableSecurity
@Configuration
@EnableGemfireRepositories
public class GemFireConf
{



    @Value("${gemfire.health.region.name}")
    private String healthRegionName;

    @Bean("Account")
    ClientRegionFactoryBean<String, Account> account(GemFireCache gemFireCache)
    {
        var bean = new ClientRegionFactoryBean<String,Account>();
        bean.setCache(gemFireCache);
        bean.setDataPolicy(DataPolicy.EMPTY);
        return bean;
    }

    @Bean("Health")
    ClientRegionFactoryBean<String, String> healthRegion(GemFireCache gemFireCache)
    {
        var bean = new ClientRegionFactoryBean<String,String>();
        bean.setCache(gemFireCache);
        bean.setDataPolicy(DataPolicy.EMPTY);
        bean.setName(healthRegionName);
        return bean;
    }

    @Bean("HealthTemplate")
    GemfireTemplate gemfireTemplate(ClientRegionFactoryBean<String, String> healthRegion)
    {
        return new GemfireTemplate(healthRegion.getRegion());
    }
}

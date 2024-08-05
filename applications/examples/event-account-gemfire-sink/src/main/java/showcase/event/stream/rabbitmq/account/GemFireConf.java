/*
 *
 *  * Copyright 2023 VMware, Inc.
 *  * SPDX-License-Identifier: GPL-3.0
 *
 */
package showcase.event.stream.rabbitmq.account;

import com.vmware.tanzu.data.services.rabbitmq.streaming.account.domain.Account;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import showcase.event.stream.rabbitmq.account.repostories.AccountRepository;

@Configuration
public class GemFireConf
{
    @Value("${event.gemfire.region.name:Account}")
    private String regionName;

    @Value("${event.gemfire.locator.host:gemfire1-locator-0.gemfire1-locator.default.svc.cluster.local}")
    private String locatorHost;

    @Value("${event.gemfire.locator.port:10334}")
    private int port = 10334;


    @Bean
    ClientCache cache()
    {
        return new ClientCacheFactory().addPoolLocator(locatorHost,port)
                .setPdxSerializer(new ReflectionBasedAutoSerializer(".*")).create();
    }

    @Bean
    Region<String, Account> accountRegion(ClientCache cache)
    {
        return (Region)cache.createClientRegionFactory(ClientRegionShortcut.PROXY).create(regionName);
    }

    @Bean
    AccountRepository accountRepository(Region<String,Account> region){
        return account -> region.put(account.getId(),account);
    }

}

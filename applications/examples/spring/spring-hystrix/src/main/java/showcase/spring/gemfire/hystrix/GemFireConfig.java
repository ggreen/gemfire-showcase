package showcase.spring.gemfire.hystrix;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;

@ClientCacheApplication
public class GemFireConfig {

    @Value("${hystrix.region.name}")
    private String regionName;

    @Bean
    Region<String,String> hystrixRegion(ClientCache clientCache){
        ClientRegionFactory<String,String> regionFactory = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY);
        return regionFactory.create(regionName);
    }
}

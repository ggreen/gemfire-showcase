package showcase.gemfire.pdx.migration;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.geode.cache.client.ClientCacheFactory;
import showcase.gemfire.pdx.migration.domain.MyData;

import java.util.Properties;

@Configuration
public class GemFireConfig {
    @Value("${gemfire.locator.host:localhost}")
    private String locatorHost;

    @Value("${gemfire.locator.port:10334}")
    private int locatorPort;

    @Value("${gemfire.region.name:PdxMigration}")
    private String regionName;


    @Bean
    Region<String, MyData> region(ClientCache cache)
    {
        return (Region)cache.createClientRegionFactory(ClientRegionShortcut.PROXY)
                .create(regionName);

    }

    @Bean
    ClientCache factory(Properties properties)
    {
        return new ClientCacheFactory()
                .addPoolLocator(locatorHost,locatorPort)
                .setPdxReadSerialized(true)
                .setPdxSerializer(new ReflectionBasedAutoSerializer(".*"))
                .create();
    }
}

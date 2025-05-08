package io.spring.gemfire.perftest.components.runner;

import com.vmware.data.services.gemfire.client.GemFireClient;
import lombok.SneakyThrows;
import nyla.solutions.core.exception.SetupException;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.SelectResults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "action", havingValue = "get")
public class GetPerfRunner implements Runnable, ApplicationListener<ContextRefreshedEvent> {

    private final GemFireClient cache;


    private final String regionName;

    private Object key;
    private Region<Object, Object> region;

    public GetPerfRunner(GemFireClient cache, @Value("${regionName}") String regionName) {
        this.cache = cache;
        this.regionName = regionName;
    }

    @SneakyThrows
    void init() {

        region = cache.getRegion(regionName);

        if (region == null) {
            throw new SetupException("Region $regionName not found");
        }


        SelectResults<Object> results = (SelectResults<Object>) cache.getClientCache().getQueryService()
                .newQuery("select * from /"+regionName+".keySet limit 1")
                .execute();

        if (results.size() == 0) {
            throw new SetupException("No data in region $regionName");

        }
        key = results.iterator().next();

    }


    @Override
    public void run() {
        region.get(this.key);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        init();
    }
}
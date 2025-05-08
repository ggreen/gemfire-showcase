package io.spring.gemfire.perftest.components.runner;

import com.vmware.data.services.gemfire.io.QuerierService;
import nyla.solutions.core.patterns.creational.generator.MapTextCreator;
import org.apache.geode.cache.Region;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * Performance action for Put, Get and Query operations
 *
 * Example arguments
 * --action=putAndGetAndQuery --batchSize=10 --keyPadLength=10 --valueLength=10
 * --seedText=TEST --queryByKey="select key from /test.entries where key = $1" --server.port=0
 *
 * @author Gregory Green
 */
/**
 *
 */
@Component
@ConditionalOnProperty(name = "action", havingValue = "putAndGetAndQuery")
public class PutAndGetAndQueryPerfRunner implements Runnable{

    private final Region<Object, Object> region;
    private final QuerierService queryService;
    private final int batchSize;
    private final int keyPadLength;
    private final int valueLength;
    private final String seedText;
    private final String queryByKey;
    private Map<String, String> map;

    public PutAndGetAndQueryPerfRunner(@Qualifier("perfTestRegion") Region<Object, Object> region,
                                       QuerierService queryService,
                                       @Value("${batchSize}") int batchSize,
                                       @Value("${keyPadLength}")
                                       int keyPadLength,
                                       @Value("${valueLength}")
                                       int valueLength,
                                       @Value("${seedText}")
                                       String seedText,
                                       @Value("${queryByKey}")
                                       String queryByKey) {
        this.region = region;
        this.queryService = queryService;
        this.batchSize = batchSize;
        this.keyPadLength = keyPadLength;
        this.valueLength = valueLength;
        this.seedText = seedText;
        this.queryByKey = queryByKey;
    }


    @Override
    public void run() {

        map = MapTextCreator.builder().size(batchSize)
                .keyPadLength(keyPadLength)
                .valueLength(valueLength)
                .seedText(seedText)
                .build().create();


        for (Map.Entry<String,String> entry : map.entrySet())
        {
            region.put(entry.getKey(),entry.getValue());
            region.get(entry.getKey());
            this.queryService.query(queryByKey,entry.getKey());
        }


    }
}

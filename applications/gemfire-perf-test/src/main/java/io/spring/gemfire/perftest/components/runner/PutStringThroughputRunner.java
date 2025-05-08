package io.spring.gemfire.perftest.components.runner;

import nyla.solutions.core.util.Text;
import org.apache.geode.cache.Region;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Performance action for Put operations with strings
 *
 * @author Gregory Green
 */
@Component
@ConditionalOnProperty(name = "action", havingValue = "putStringThroughput")
public class PutStringThroughputRunner implements Runnable{
    private int putCount;
    private final int maxCountPerThread;
    private final String valueString;

    private final Region<String, String> region;
    private final String keyPrefix;

    public PutStringThroughputRunner(
            @Value("${valueLength}")
            int valueSize,
            @Value("${keyPrefix}")
            String keyPrefix,
            @Value("${maxCountPerThread}")
            int maxCountPerThread,
            @Qualifier("perfTestRegion")
            Region<String, String> region) {
        this.putCount  =0;
        this.region = region;
        this.maxCountPerThread = maxCountPerThread;
        this.keyPrefix = keyPrefix;
        valueString = Text.generateAlphabeticId(valueSize);
    }

    @Override
    public void run() {
        putCount += 1;
        putCount %= maxCountPerThread;

        region.put(keyPrefix+"-"+putCount+"-"+Thread.currentThread().getId(), valueString);
    }

}

package io.spring.gemfire.perftest.components.runner;

import nyla.solutions.core.util.Text;
import org.apache.geode.cache.Region;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "action", havingValue = "putStringThroughput")
public class PutStringThroughputRunner implements Runnable{
    private int putCount;
    private final int maxCountPerThread;
    private final String valueString;

    private Region<String, String> region;
    private final String keyPrefix;

    public PutStringThroughputRunner(int valueSize, String keyPrefix, int maxCountPerThread) {
        this.putCount  =0;

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

    public void setRegion(Region<String, String> region) {
        this.region = region;
    }
}

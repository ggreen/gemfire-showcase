package io.spring.gemfire.perftest.components.runner;

import jakarta.annotation.Resource;
import nyla.solutions.core.patterns.creational.generator.MapTextCreator;
import org.apache.geode.cache.Region;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "action", havingValue = "putAllString")
public class PutAllStringPerfRunner implements  Runnable{

    private final int keyPadLength;
    private final int batchSize;
    private final int valueLength;
    private final String seedText;

    private final Region<String, String> region;

    private final Map<String, String> map;

    public PutAllStringPerfRunner(
            @Value("${batchSize}") int batchSize,
            @Value("${keyPadLength}")
            int keyPadLength,
            @Value("${valueLength}")
            int valueLength,
            @Value("${seedText}")
            String seedText,
            @Qualifier("perfTestRegion")
            Region<String, String> region) {

        this.batchSize = batchSize;
        this.keyPadLength = keyPadLength;
        this.valueLength = valueLength;
        this.seedText = seedText;
        this.region = region;

        map = MapTextCreator.builder().size(batchSize)
                .keyPadLength(keyPadLength)
                .valueLength(valueLength)
                .seedText(seedText)
                .build().create();


    }



    void init() {

        init();
    }

    public void run() {
        region.putAll(map);
    }

    Map<String, String> getMap(){
        return map;
    }
}
package io.spring.gemfire.perftest.components.runner;

import jakarta.annotation.Resource;
import nyla.solutions.core.util.Digits;
import nyla.solutions.core.util.Text;
import org.apache.geode.cache.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static java.lang.String.valueOf;

/**
 * Performance action for Put operations with strings
 *
 * @author Gregory Green
 */
@Component
@ConditionalOnProperty(name = "action", havingValue = "putString")
public class PutStringInRegionRunner implements Runnable {


    private final int valueSize;
    private final int min;
    private final int max;
    private final String valueString;
    private final String keyId;

    @Resource(name = "perfTestRegion")
    private Region<String, String> region;

    public PutStringInRegionRunner(@Value("${valueSize}") int valueSize,
                                   @Value("${startKeyValue}")
                                   int min,
                                   @Value("${endKeyValue}")
                                   int max) {
        this.valueSize = valueSize;
        this.min = min;
        this.max = max;

        this.valueString = Text.generateAlphabeticId(valueSize);
        this.keyId = valueOf(new Digits().generateInteger(min, max));
    }

    public void run() {
        region.put(valueOf(keyId + Thread.currentThread().getId()), valueString);
    }

    public void setRegion(Region<String, String> region) {
        this.region = region;
    }
}

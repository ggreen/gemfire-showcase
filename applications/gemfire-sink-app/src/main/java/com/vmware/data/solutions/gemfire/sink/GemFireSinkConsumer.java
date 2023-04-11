package com.vmware.data.solutions.gemfire.sink;

import com.vmware.data.services.gemfire.serialization.PDX;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.pdx.PdxInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Consumer;

import static java.lang.String.valueOf;

/**
 * @author Gregory Green
 */
@Component
@Slf4j
public class GemFireSinkConsumer implements Consumer<String> {

    private final Map<String, PdxInstance> region;
    private final PDX pdx;
    private final String keyFieldExpression;
    private final String valuePdxClassName;

    public GemFireSinkConsumer(Map<String, PdxInstance> region,
                               PDX pdx,
                               @Value("${keyFieldExpression:id}")
                               String keyFieldExpression,
                               @Value("${valuePdxClassName:java.lang.Object}")
                               String valuePdxClassName) {
        this.region = region;
        this.pdx = pdx;
        this.keyFieldExpression = keyFieldExpression;
        this.valuePdxClassName = valuePdxClassName;
    }

    @Override
    public void accept(String json) {
        log.info(json);

        var formattedType = pdx.addTypeToJson(json,valuePdxClassName);
        var pdxInstance = pdx.fromJSON(formattedType);
        var key = pdxInstance.getField(keyFieldExpression);

        region.put(valueOf(key),pdxInstance);
    }
}

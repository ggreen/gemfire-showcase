package com.vmware.data.solutions.gemfire.sink;

import com.vmware.data.services.gemfire.serialization.GemFireJson;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.json.JsonDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Gregory Green
 */
@Component
@Slf4j
public class GemFireSinkConsumer implements Consumer<String> {

    private final Map<String, JsonDocument> region;
    private final GemFireJson gemFireJson;
    private final String keyFieldExpression;
    private final String valuePdxClassName;

    public GemFireSinkConsumer(Map<String, JsonDocument> region,
                               GemFireJson gemFireJson,
                               @Value("${keyFieldExpression:id}")
                               String keyFieldExpression,
                               @Value("${valuePdxClassName:java.lang.Object}")
                               String valuePdxClassName) {
        this.region = region;
        this.gemFireJson = gemFireJson;
        this.keyFieldExpression = keyFieldExpression;
        this.valuePdxClassName = valuePdxClassName;
    }

    @Override
    public void accept(String json) {
        log.info(json);

        var formattedType = gemFireJson.addTypeToJson(json,valuePdxClassName);
//        var pdxInstance = gemFireJson.fromJSON(formattedType);
                //gemFireJson.fromJSON(formattedType);
//        var key = pdxInstance..getField(keyFieldExpression);
//        region.put(valueOf(key),pdxInstance);
    }
}

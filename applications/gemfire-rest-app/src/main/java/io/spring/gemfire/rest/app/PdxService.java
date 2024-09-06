package io.spring.gemfire.rest.app;

import com.vmware.data.services.gemfire.serialization.GemFireJson;
import org.apache.geode.json.JsonDocument;
import org.apache.geode.json.JsonDocumentFactory;
import org.springframework.stereotype.Component;

/**
 * @author Gregory Green
 */
public class PdxService
{
    private final GemFireJson gemFireJson;

    public PdxService(JsonDocumentFactory factory)
    {
        this(new GemFireJson(factory));
    }

    public PdxService(GemFireJson gemFireJson)
    {
        this.gemFireJson = gemFireJson;
    }

    public JsonDocument fromJSON(String value)
    {
        return gemFireJson.fromJSON(value);
    }

    public String toJSON(JsonDocument value, String type)
    {
        return value.toJson();
    }
}

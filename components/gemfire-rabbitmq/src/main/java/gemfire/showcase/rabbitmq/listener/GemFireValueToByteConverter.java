package gemfire.showcase.rabbitmq.listener;


import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * Converts GemFire PDXInstance to byte array
 * @author Gregory Green
 */
public class GemFireValueToByteConverter implements Function<Object,byte[]> {
    @Override
    public byte[] apply(Object sourceObject) {
        if(sourceObject == null)
            return null;

        if(sourceObject instanceof PdxInstance pdxInstance)
        {
            var json = JSONFormatter.toJSON(pdxInstance);
            return json != null? json.getBytes(StandardCharsets.UTF_8): null;
        }

        return String.valueOf(sourceObject).getBytes(StandardCharsets.UTF_16);
    }
}

package gemfire.showcase.rabbitmq.listener;


import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * Converts GemFire PDXInstance to byte array
 * @author Gregory Green
 */
public class GemFireValueToByteConverter implements Function<Object,byte[]> {
    private final Logger log = LogManager.getLogger(GemFireValueToByteConverter.class);

    @Override
    public byte[] apply(Object sourceObject) {
        if(sourceObject == null)
            return null;

        log.info("Converting sourceObject of type: {}",sourceObject.getClass().getName());

        if(sourceObject instanceof PdxInstance pdxInstance)
        {
            var json = JSONFormatter.toJSON(pdxInstance);
            log.info("Sending UTF_8 Json : {}",json);

            return json != null? json.getBytes(StandardCharsets.UTF_8): null;
        }

        var payload = sourceObject.toString();
        log.info("Sending UTF_8 payload: {}",payload);

        return payload.getBytes(StandardCharsets.UTF_8);
    }
}

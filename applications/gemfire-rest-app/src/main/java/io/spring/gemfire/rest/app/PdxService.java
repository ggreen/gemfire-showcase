package io.spring.gemfire.rest.app;

import com.vmware.data.services.gemfire.serialization.PDX;
import org.apache.geode.pdx.PdxInstance;
import org.springframework.stereotype.Component;

/**
 * @author Gregory Green
 */
@Component
public class PdxService
{
    private final PDX pdx;

    public PdxService()
    {
        this(new PDX());
    }

    public PdxService(PDX pdx)
    {
        this.pdx = pdx;
    }

    public PdxInstance fromJSON(String value)
    {
        return pdx.fromJSON(value);
    }

    public String toJSON(PdxInstance value, String type)
    {
        return pdx.toJSON(value,type);
    }
}

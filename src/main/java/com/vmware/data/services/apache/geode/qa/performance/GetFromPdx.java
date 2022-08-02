package com.vmware.data.services.apache.geode.qa.performance;

import org.apache.geode.pdx.PdxInstance;

import java.util.function.Function;

/**
 * @author Gregory Green
 */
public class GetFromPdx implements Function<PdxInstance,String>
{
    public GetFromPdx(String idField)
    {
    }

    @Override
    public String apply(PdxInstance pdxInstance)
    {
        return null;
    }
}

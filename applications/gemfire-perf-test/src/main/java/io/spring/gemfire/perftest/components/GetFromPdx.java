package io.spring.gemfire.perftest.components;


import org.apache.geode.pdx.PdxInstance;

import java.util.function.Function;
/**
 * @author Gregory Green
 */
public class GetFromPdx implements Function<PdxInstance, String> {

    private final String idField;

    public GetFromPdx(String idField)
    {
        this.idField = idField;
    }


    @Override
    public String apply(PdxInstance pdxInstance)
    {
        return String.valueOf(pdxInstance.getField(idField));
    }
}

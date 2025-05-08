package io.spring.gemfire.perftest.components.runner;

import nyla.solutions.core.patterns.conversion.Converter;
import nyla.solutions.core.patterns.creational.generator.json.JsonGeneratorCreator;
import org.apache.geode.cache.Region;
import org.apache.geode.pdx.PdxInstance;

import java.util.function.Function;

/**
 * Performance action for Put operations
 * @author Gregory Green
 */
public class PdxJsonPutPerfRunner<K> implements Runnable
{
    private final JsonGeneratorCreator jsonGeneratorCreator;
    private final Region<K, PdxInstance> region;
    private final Converter<String, PdxInstance> converter;
    private final Function<PdxInstance,K > getIdFunc;

    public PdxJsonPutPerfRunner(JsonGeneratorCreator jsonGeneratorCreator,
                                Converter<String, PdxInstance> converter,
                                Region<K,PdxInstance> region,
                                Function<PdxInstance,K > getIdFunc)
    {
        this.jsonGeneratorCreator = jsonGeneratorCreator;
        this.converter = converter;
        this.region = region;
        this.getIdFunc = getIdFunc;
    }

    public void run()
    {
        PdxInstance pdxInstance = converter.convert(
                jsonGeneratorCreator.create());

        this.region.put(this.getIdFunc.apply(pdxInstance),
                pdxInstance);

    }
}

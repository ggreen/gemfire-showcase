package com.vmware.data.services.gemfire.performance;

import com.vmware.data.services.gemfire.qa.performance.PdxJsonPutPerfRunner;
import nyla.solutions.core.patterns.conversion.Converter;
import nyla.solutions.core.patterns.creational.generator.json.JsonGeneratorCreator;
import org.apache.geode.cache.Region;
import org.apache.geode.pdx.PdxInstance;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.mockito.Mockito.*;

class PdxJsonPutPerfRunnerTest
{

    @Test
    void perftest()
    {
        JsonGeneratorCreator jsonGeneratorCreator = mock(JsonGeneratorCreator.class);
        Region<?,?> mockRegion = mock(Region.class);
        Converter<String, PdxInstance> mockConverter = mock(Converter.class);
        Function<PdxInstance,String> getIdFunc = mock(Function.class);

        PdxJsonPutPerfRunner pdxJsonPutPerfRunner = new PdxJsonPutPerfRunner(jsonGeneratorCreator,mockConverter,mockRegion,getIdFunc);

        pdxJsonPutPerfRunner.run();
        verify(getIdFunc).apply(any());
        verify(jsonGeneratorCreator).create();
        verify(mockRegion).put(any(),any());
    }
}
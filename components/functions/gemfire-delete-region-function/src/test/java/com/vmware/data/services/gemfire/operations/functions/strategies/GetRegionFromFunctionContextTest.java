package com.vmware.data.services.gemfire.operations.functions.strategies;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GetRegionFromFunctionContextTest {

    private GetRegionFromFunctionContext subject;
    @Mock
    private RegionFunctionContext regionFunctionContext;

    @Mock
    private FunctionContext functionContext;

    @Mock
    private Region<Object,Object> region;

    @Mock
    private Cache cache;

    @BeforeEach
    void setUp() {

        subject = new GetRegionFromFunctionContext( () -> cache);
    }

    @Test
    void execute_whenRegionFunctionContext() {


        when(regionFunctionContext.getDataSet()).thenReturn(region);

        var actual = subject.apply(regionFunctionContext);

        assertThat(actual).isEqualTo(region);
    }

    @Test
    void execute_whenFunctionContext_thenException() {

        String[] args = {"region"};

        try {
            var actual = subject.apply(functionContext);
            fail("No allowed");
        }
        catch (FunctionException e)
        {
            assertThat(e.getMessage()).contains("region");
        }

    }
}
package com.vmware.data.services.gemfire.operations.functions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteFunctionTest {

    private DeleteFunction subject;

    @Mock
    private FunctionContext context;
    @Mock
    private Region<Object,Object> region;

    @Mock
    private Function<FunctionContext, Collection<Object>> getResults;
    @Mock
    private Function<FunctionContext, Region<Object, Object>> getRegion;


    @BeforeEach
    void setUp() {
        subject = new DeleteFunction(getRegion,getResults);
    }

    @Test
    void delete() {

        Collection<Object> results = Arrays.asList("T");

        when(getResults.apply(any())).thenReturn(results);
        when(getRegion.apply(any())).thenReturn(region);

        subject.execute(context);

        verify(region).removeAll(any());
    }
}
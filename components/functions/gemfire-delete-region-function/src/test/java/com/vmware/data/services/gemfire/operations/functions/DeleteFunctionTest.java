package com.vmware.data.services.gemfire.operations.functions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteFunctionTest {

    private Logger logger  = LogManager.getLogger(DeleteFunction.class);
    private DeleteFunction subject;

    @Mock
    private FunctionContext context;
    @Mock
    private Region<Object,Object> region;

    @Mock
    private Function<FunctionContext, Collection<Object>> getResults;
    @Mock
    private Function<FunctionContext, Region<Object, Object>> getRegion;

    @Mock
    private ResultSender rs;


    @BeforeEach
    void setUp() {
        subject = new DeleteFunction(getRegion,getResults);
    }

    @Test
    void delete_whenArgsAreNull() {

        try
        {
            subject.execute(context);
            fail();
        }
        catch (FunctionException e)
        {
            assertThat(e.getMessage()).contains("arguments");
        }

    }
    @Test
    void delete() {

        String[] args = {"oql"};
        Collection<Object> results = Arrays.asList("T");

        when(getResults.apply(any())).thenReturn(results);
        when(getRegion.apply(any())).thenReturn(region);
        when(context.getResultSender()).thenReturn(rs);
        when(context.getArguments()).thenReturn(args);

        subject.execute(context);

        verify(region).removeAll(any());
    }

    @Test
    void useBatchSize() {

        String[] argsWithBatchSize = {"oql","2"};

        Collection<Object> results = Arrays.asList("1","2","3");

        when(getResults.apply(any())).thenReturn(results);
        when(getRegion.apply(any())).thenReturn(region);
        when(context.getArguments()).thenReturn(argsWithBatchSize);
        when(context.getResultSender()).thenReturn(rs);

        subject.execute(context);

        verify(region,times(2)).removeAll(any());
    }
}
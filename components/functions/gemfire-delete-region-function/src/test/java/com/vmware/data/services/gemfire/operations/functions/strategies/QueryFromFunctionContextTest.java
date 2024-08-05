package com.vmware.data.services.gemfire.operations.functions.strategies;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueryFromFunctionContextTest {

    private QueryFromFunctionContext subject;
    @Mock
    private RegionFunctionContext rfc;
    private Collection<Object> results = Arrays.asList("k1");
    @Mock
    private Region<Object,Object> region;
    @Mock
    private Cache cache;
    @Mock
    private QueryService qs;
    @Mock
    private Query query;

    @BeforeEach
    void setUp() {
        subject = new QueryFromFunctionContext();
    }

    @Test
    void apply_RegionFunctionContext() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {

        String[] args = {"oql"};

//        when(rfc.getDataSet()).thenReturn(region);
        when(rfc.getCache()).thenReturn(cache);
        when(rfc.getArguments()).thenReturn(args);
        when(cache.getQueryService()).thenReturn(qs);
        when(qs.newQuery(anyString())).thenReturn(query);
        when(query.execute(any(RegionFunctionContext.class))).thenReturn(results);

        var actual = subject.apply(rfc);

        assertThat(actual).isEqualTo(results);
    }
}
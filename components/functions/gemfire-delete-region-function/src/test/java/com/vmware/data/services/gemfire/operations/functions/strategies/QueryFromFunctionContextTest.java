package com.vmware.data.services.gemfire.operations.functions.strategies;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Mock
    private FunctionContext fc;

    @BeforeEach
    void setUp() {
        subject = new QueryFromFunctionContext();
    }

    @Test
    void apply_RegionFunctionContext() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {

        String[] args = {"oql"};

        when(rfc.getCache()).thenReturn(cache);
        when(rfc.getArguments()).thenReturn(args);
        when(cache.getQueryService()).thenReturn(qs);
        when(qs.newQuery(anyString())).thenReturn(query);
        when(query.execute(any(RegionFunctionContext.class))).thenReturn(results);

        var actual = subject.apply(rfc);

        assertThat(actual).isEqualTo(results);
    }

    @DisplayName("Given no args when apply Then throw exception")
    @Test
    void apply_noArgs() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {

        when(rfc.getCache()).thenReturn(cache);
        when(cache.getQueryService()).thenReturn(qs);

        try {
            var actual = subject.apply(rfc);
            fail("Not allows");
        }
        catch (FunctionException e)
        {
            assertThat(e.getMessage()).contains("arguments");
        }

    }

    @DisplayName("Given empty args when apply Then throw exception")
    @Test
    void apply_emptyArgs() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {

        String[] emptyArgs= {};
        when(rfc.getCache()).thenReturn(cache);
        when(cache.getQueryService()).thenReturn(qs);
        when(rfc.getArguments()).thenReturn(emptyArgs);

        try {
            var actual = subject.apply(rfc);
            fail("Not allows");
        }
        catch (FunctionException e)
        {
            assertThat(e.getMessage()).contains("arguments");
        }

    }

    @DisplayName("Given function context without region and no owl When apply Then return results")
    @Test
    void apply_notOnRegionArgs() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {

        Collection<Object> expected = Arrays.asList("key1");
        String[] args= {"oql"};
        when(fc.getCache()).thenReturn(cache);
        when(cache.getQueryService()).thenReturn(qs);
        when(fc.getArguments()).thenReturn(args);
        when(qs.newQuery(anyString())).thenReturn(query);
        when(query.execute()).thenReturn(expected);

        var actual = subject.apply(fc);
        assertEquals(expected, actual);

    }

    @DisplayName("Given function context without region and no owl When apply Then return results")
    @Test
    void apply_onRegionArgs() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {

        Collection<Object> expected = Arrays.asList("key1");
        String[] args= {"oql"};
        when(rfc.getCache()).thenReturn(cache);
        when(cache.getQueryService()).thenReturn(qs);
        when(rfc.getArguments()).thenReturn(args);
        when(qs.newQuery(anyString())).thenReturn(query);
        when(query.execute(any(RegionFunctionContext.class))).thenReturn(expected);

        var actual = subject.apply(rfc);
        assertEquals(expected, actual);

    }

    @Test
    void apply_exceptionWithOql() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {

        String oql = "select * from /Region";

        String[] args= {oql};
        when(rfc.getCache()).thenReturn(cache);
        when(cache.getQueryService()).thenReturn(qs);
        when(rfc.getArguments()).thenReturn(args);
        when(qs.newQuery(anyString())).thenReturn(query);
        when(query.execute(any(RegionFunctionContext.class))).thenThrow(new RuntimeException("Query error"));

        try{

            subject.apply(rfc);
            fail("Not allowed");
        }
        catch (FunctionException e)
        {
            assertThat(e.getMessage()).contains(oql);
        }
    }
}
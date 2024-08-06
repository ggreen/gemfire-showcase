package com.vmware.data.services.gemfire.operations.functions.strategies;

import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.query.FunctionDomainException;
import org.apache.geode.cache.query.NameResolutionException;
import org.apache.geode.cache.query.QueryInvocationTargetException;
import org.apache.geode.cache.query.TypeMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetArgsTest {

    private GetArgs subject;
    private String oql = "oql";

    @Mock
    private FunctionContext fc;
    private int batchSize = 3;

    @BeforeEach
    void setUp() {
        subject = new GetArgs();
    }

    @Test
    void getOql() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {

        String[] args = {oql};
        when(fc.getArguments()).thenReturn(args);
        assertEquals(oql,subject.getOql(fc));

    }

    @Test
    void getBatchSize() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {

        String[] args = {oql,String.valueOf(batchSize)};
        when(fc.getArguments()).thenReturn(args);
        assertEquals(batchSize,subject.getBatchSize(fc));

    }
}
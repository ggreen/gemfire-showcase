package com.vmware.data.services.gemfire.operations.functions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteFunctionTest {

    private DeleteFunction subject;

    @Mock
    private FunctionContext context;
    @Mock
    private Region<Object,Object> region;


    @BeforeEach
    void setUp() {
//        subject = new DeleteFunction();
    }

    @Test
    void delete() {

        subject.execute(context);

        verify(region).removeAll(any());
    }
}
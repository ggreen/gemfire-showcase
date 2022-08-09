package com.vmware.data.services.gemfire.io.function;

import nyla.solutions.core.util.Organizer;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.ResultCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FuncExeTest
{
    private FuncExe subject;
    private Execution<?,?,?> execution;
    private Region<?,?> region;
    private ResultCollector resultCollector;
    private Collection<String> resultCollection;

    @BeforeEach
    void setUp()
    {
        resultCollection = Arrays.asList("Hello","world");
        execution = mock(Execution.class);
        subject = new FuncExe(execution);
        region = mock(Region.class);
        resultCollector = mock(ResultCollector.class);
        when(execution.execute(any(Function.class))).thenReturn(resultCollector);
        when(resultCollector.getResult()).thenReturn(resultCollection);
    }

    @Test
    void getExecution()
    {
        Execution<?,?,?> actualExecution = subject.getExecution();
        assertEquals(execution,actualExecution);
    }

    @Test
    void withFilter()
    {
        Set<String> set = Organizer.toSet("Hello","World");
        FuncExe actual = subject.withFilter(set);
        assertNotNull(actual);
        verify(execution).withFilter(any());
    }

    @Test
    void withCollector()
    {
        ResultCollector<?,?> collector = mock(ResultCollector.class);
        FuncExe actual = subject.withCollector(collector);
        assertNotNull(actual);
        verify(execution).withCollector(any());
    }

    @Test
    void setArguments()
    {
        String[] args = {"assa"};
        FuncExe actual = subject.setArguments(args);
        assertNotNull(actual);
        verify(execution).setArguments(any());
    }

    @Test
    void execute() throws Exception
    {
        Function function = mock(Function.class);
        Collection<Object> results = subject.exe(function);
        assertNotNull(results);
    }
}
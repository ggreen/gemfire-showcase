package com.vmware.data.services.gemfire.operations.functions.touch;

import nyla.solutions.core.util.Organizer;
import org.apache.geode.LogWriter;
import org.apache.geode.cache.CacheTransactionManager;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TouchFunctionTest {

    @Mock
    private LogWriter logger;

    @Mock
    private Region<Object, Object> region;

    @Mock
    private RegionAttributes<Object, Object> regionAttributes;

    @Mock
    private RegionFunctionContext context;

    @Mock
    private DataPolicy dataPolicy;

    @Mock
    private ResultSender<Object> resultSender ;

    @Mock
    private java.util.function.Function<RegionFunctionContext, Region<Object, Object>> regionGetter;

    @Mock
    private TouchFunction subject;

    @Mock
    private CacheTransactionManager cacheTransactionManager;

    private Supplier<CacheTransactionManager> txMgrSupplier =
            () -> cacheTransactionManager;

    private Set<Object> keySet = Organizer.toSet("hello");
    private Object value = "world";
    private boolean copyOnRead = false;

    @BeforeEach
    public void setUp() throws Exception
    {

        subject =  new TouchFunction(
                logger,
                regionGetter,
                txMgrSupplier,
                copyOnRead);
    }

    @Test
    public void execute()
    {
        when(context.getDataSet()).thenReturn(region);
        when(region.getAttributes()).thenReturn(regionAttributes);
        when(regionAttributes.getDataPolicy()).thenReturn(dataPolicy);
        when(dataPolicy.withPartitioning()).thenReturn(true);
        when(regionGetter.apply(any())).thenReturn(region);
        when(context.getResultSender()).thenReturn(resultSender);
        when(region.keySet()).thenReturn(keySet);
        when(region.get(any())).thenReturn(value);

        subject.execute(context);
        verify(region).put(any(),any());
    }

    @Test
    void getId() {
        assertEquals("Touch", subject.getId());
    }

    @Test
    void hasResult() {
        assertEquals(false, subject.hasResult());
    }

    @Test
    void isHA() {
        assertEquals(true, subject.isHA());
    }

    @Test
    void optimizeForWrite() {
        assertEquals(true, subject.optimizeForWrite());
    }

    @Test
    void init() {
    }
}
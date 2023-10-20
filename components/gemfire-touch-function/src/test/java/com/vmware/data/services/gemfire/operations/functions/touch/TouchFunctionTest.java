package com.vmware.data.services.gemfire.operations.functions.touch;

import nyla.solutions.core.util.Organizer;
import org.apache.geode.cache.CacheTransactionManager;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.function.Supplier;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for TouchFunction
 * @author gregory green
 */
@ExtendWith(MockitoExtension.class)
class TouchFunctionTest {

    @Mock
    private Logger logger;

    @Mock
    private Region<Object, Object> region;

    @Mock
    private RegionAttributes<Object, Object> regionAttributes;

    @Mock
    private RegionFunctionContext context;

    @Mock
    private DataPolicy dataPolicy;

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
    private long reportIntervalMs = 10L * 1000L;
    private long targetRate = 10 ;
    private int batchSize = 100;

    @BeforeEach
    public void setUp() throws Exception
    {

        subject =  new TouchFunction(
                logger,
                regionGetter,
                txMgrSupplier,
                copyOnRead,
                reportIntervalMs,
                targetRate,
                batchSize);
    }

    @Test
    void stackTrace() {

        Throwable e = new SecurityException();
        assertNotNull(subject.stackTrace(e));
    }

    @Test
    public void execute()
    {
        when(context.getDataSet()).thenReturn(region);
        when(region.getAttributes()).thenReturn(regionAttributes);
        when(regionAttributes.getDataPolicy()).thenReturn(dataPolicy);
        when(dataPolicy.withPartitioning()).thenReturn(true);
        when(regionGetter.apply(any())).thenReturn(region);
        when(region.keySet()).thenReturn(keySet);
        when(region.get(any())).thenReturn(value);

        subject.execute(context);
        verify(region).put(any(),any());
    }

    @DisplayName("GIVEN keys > batch size when execute then count")
    @Test
    public void execute_batchSize()
    {
        int expectedBatchSize = 3;

        subject =  new TouchFunction(
                logger,
                regionGetter,
                txMgrSupplier,
                copyOnRead,
                reportIntervalMs,
                targetRate,
                expectedBatchSize);

        when(context.getDataSet()).thenReturn(region);
        when(region.getAttributes()).thenReturn(regionAttributes);
        when(regionAttributes.getDataPolicy()).thenReturn(dataPolicy);
        when(dataPolicy.withPartitioning()).thenReturn(true);
        when(regionGetter.apply(any())).thenReturn(region);
        Set<Object> expectedKeySet = Organizer.toSet("1","2","3","4","5");
        when(region.keySet()).thenReturn(expectedKeySet);
        when(region.get(any())).thenReturn(value);

        subject.execute(context);
        verify(region,times(expectedKeySet.size())).put(any(),any());
    }


    @DisplayName("GIVEN nonRegionFunctionContext WHEN execute then throw FunctionException")
    @Test
    public void execute_nonRegionFunctionContext()
    {
        FunctionContext notRegionFunctionContext = mock(FunctionContext.class);
        try {
            subject.execute(notRegionFunctionContext);
            fail("Must throw exception");
        }
        catch (FunctionException e)
        {
            assertThat(e.toString()).contains("TouchFunction must be executed on a region. Ex: execute function --id=TouchFunction --region=/myRegion");
        }

    }

    @Test
    void getId() {
        assertEquals("TouchFunction", subject.getId());
    }

    @Test
    void hasResult() {
        assertEquals(false, subject.hasResult());
    }

    @Test
    void isHA() {
        assertEquals(false, subject.isHA());
    }

    @Test
    void optimizeForWrite() {
        assertEquals(true, subject.optimizeForWrite());
    }

    @DisplayName("Given Expected Value When getConfigInt Then Return Expected")
    @Test
    void getConfigInt() {
        int expected = 25;
        var actual = TouchFunction.getConfigInt("getConfigInt",expected);

        assertEquals(expected, actual);
    }


    @DisplayName("Given system property When GetConfigInt Then Return System")
    @Test
    void getConfigInt_returnSystemProperty() {
        Integer expected = Integer.valueOf(25);
        System.setProperty("getConfigInt_returnSystemProperty",expected.toString());
        var actual = TouchFunction.getConfigInt("getConfigInt_returnSystemProperty",expected+232);

        assertEquals(expected, actual);
    }


    @DisplayName("Given Expected Value When getConfigLong Then Return Expected")
    @Test
    void getConfigLong() {
        long expected = 56;
        var actual = TouchFunction.getConfigLong("getConfigLong",expected);

        assertEquals(expected, actual);
    }


    @DisplayName("Given system property When getConfigLong Then Return System")
    @Test
    void getConfigLong_returnSystemProperty() {
        Long expected = Long.valueOf(25L);
        System.setProperty("getConfigLong_returnSystemProperty",expected.toString());
        var actual = TouchFunction.getConfigLong("getConfigLong_returnSystemProperty",expected+232);

        assertEquals(expected, actual);
    }
    @Test
    void init() {
    }

    @Test
    void testToString() {

        assertThat(subject.toString()).contains(valueOf(this.batchSize));
        assertThat(subject.toString()).contains(valueOf(this.copyOnRead));
        assertThat(subject.toString()).contains(valueOf(this.reportIntervalMs));
        assertThat(subject.toString()).contains(valueOf(this.targetRate));
    }
}
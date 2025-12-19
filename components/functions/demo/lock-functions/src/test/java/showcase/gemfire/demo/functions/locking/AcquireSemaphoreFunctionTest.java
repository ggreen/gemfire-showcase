package showcase.gemfire.demo.functions.locking;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcquireSemaphoreFunctionTest {

    private AcquireSemaphoreFunction subject;

    @Mock
    private RegionFunctionContext rfc;
    private Object lockKey =  "lock";
    @Mock
    private ResultSender resultSender;
    @Mock
    private Semaphore semaphore;
    @Mock
    private Region region;

    private final String[] args = {"1","5", TimeUnit.SECONDS.toString()};
    private final Set<?> keySet = Set.of(lockKey);
    private Function<Integer, Semaphore> function;


    @BeforeEach
    void setUp() {
        function = permit -> semaphore;
        subject = new AcquireSemaphoreFunction(function);
    }

    @Test
    void given_lock_exists_then_acquired() throws InterruptedException {


        when(rfc.getArguments()).thenReturn(args);
        when(rfc.getFilter()).thenReturn(keySet);
        when(rfc.getDataSet()).thenReturn(region);
        when(rfc.getResultSender()).thenReturn(resultSender);
        when(region.get(any())).thenReturn(semaphore);
        when(region.get(any())).thenReturn(semaphore);

        subject.execute(rfc);

        verify(semaphore).tryAcquire(anyLong(),any(TimeUnit.class));

    }

    @Test
    void given_lock_does_exists_then_create() throws InterruptedException {

        when(rfc.getArguments()).thenReturn(args);
        when(rfc.getFilter()).thenReturn(keySet);
        when(rfc.getDataSet()).thenReturn(region);
        when(rfc.getResultSender()).thenReturn(resultSender);
        when(region.get(any())).thenReturn(null).thenReturn(semaphore);


        subject.execute(rfc);

        verify(region).put(any(),any());
        verify(semaphore).tryAcquire(anyLong(),any(TimeUnit.class));
    }

}
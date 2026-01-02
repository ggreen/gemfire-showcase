package showcase.gemfire.demo.functions.locking;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
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
    void given_2threads_when_execute_then_atLeast_one_locks() throws InterruptedException {

        Semaphore realSemaphore = new Semaphore(1);

        function = permit -> realSemaphore;
        subject = new AcquireSemaphoreFunction(function);

        when(rfc.getArguments()).thenReturn(args).thenReturn(args);
        when(rfc.getFilter()).thenReturn(keySet).thenReturn(keySet);
        when(rfc.getDataSet()).thenReturn(region).thenReturn(region);
        when(rfc.getResultSender()).thenReturn(resultSender).thenReturn(resultSender);
        when(region.get(lockKey)).thenReturn(realSemaphore).thenReturn(realSemaphore);


        ExecutorService executor = Executors.newFixedThreadPool(2);
        final AtomicInteger count = new AtomicInteger(0);

        executor.submit(() -> { subject.execute(rfc); count.incrementAndGet();});
        executor.submit(() -> { subject.execute(rfc); count.incrementAndGet();});

        executor.awaitTermination(3,TimeUnit.SECONDS);
        assertThat(count.get()).isEqualTo(1);

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

    @Test
    void givenNoFilter_when_execute_then_ErrorFilterRequired() {

        try{
            subject.execute(rfc);
        }
        catch (FunctionException e){
            assertThat(e.getMessage()).contains("filter").contains("required");
        }
    }

    @Test
    void givenArgument_when_execute_then_NoArgumentsRequired() {

        when(rfc.getFilter()).thenReturn(this.keySet);

        try{
            subject.execute(rfc);
        }
        catch (FunctionException e){
            assertThat(e.getMessage()).contains("required").contains("input")
                    .contains(TimeUnit.MINUTES.toString())
            .contains(TimeUnit.SECONDS.toString());
        }
    }


    @Test
    void optimizedWrite() {
        assertThat(subject.optimizeForWrite()).isTrue();
    }
}
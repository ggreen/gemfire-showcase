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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReleaseSemaphoreFunctionTest {


    private ReleaseSemaphoreFunction subject;
    private Object lockKey = "lock";
    @Mock
    private RegionFunctionContext<String[]> rfc;
    @Mock
    private ResultSender resultSender;
    @Mock
    private Semaphore semaphore;
    @Mock
    private Region<Object, Object> region;

    @BeforeEach
    void setUp() {
        subject = new ReleaseSemaphoreFunction();
    }

    @Test
    void unlock() {


        String[] args = { "lockService","1", "1"};
        Set keySet = Set.of(lockKey);
        when(rfc.getFilter()).thenReturn(keySet);
        when(rfc.getDataSet()).thenReturn(region);
        when(region.get(any())).thenReturn(semaphore);
        when(rfc.getResultSender()).thenReturn(resultSender);

        subject.execute(rfc);

        verify(semaphore).release();
        verify(resultSender).lastResult(any());
        verify(region).remove(any());

    }

    @Test
    void optimizedWrite() {
        assertThat(subject.optimizeForWrite()).isTrue();
    }
}
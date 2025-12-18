package showcase.gemfire.demo.functions.locking;

import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.distributed.DistributedLockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnLockFunctionTest {


    private UnLockFunction subject;
    private Object lockKey = "lock";
    @Mock
    private RegionFunctionContext<String[]> rfc;
    @Mock
    private ResultSender resultSender;
    @Mock
    private DistributedLockService distributedLockService;

    @BeforeEach
    void setUp() {
        subject = new UnLockFunction(lockService -> distributedLockService);
    }

    @Test
    void unlock() {


        String[] args = { "lockService","1", "1"};
        Set keySet = Set.of(lockKey);
        when(rfc.getArguments()).thenReturn(args);
        when(rfc.getFilter()).thenReturn(keySet);
        when(rfc.getResultSender()).thenReturn(resultSender);

        subject.execute(rfc);

        verify(distributedLockService).unlock(any());

    }
}
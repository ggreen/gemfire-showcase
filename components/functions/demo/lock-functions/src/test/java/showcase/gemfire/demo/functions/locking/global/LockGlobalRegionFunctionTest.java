package showcase.gemfire.demo.functions.locking.global;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LockGlobalRegionFunctionTest {

    private LockGlobalRegionFunction subject;
    @Mock
    private RegionFunctionContext<String[]> rfc;
    @Mock
    private Lock lock;

    @Mock
    private Region<Object, Object> region;

    private final Map<Object, Lock> map = new ConcurrentHashMap<>();
    private Object key = "lock";


    @BeforeEach
    void setUp() {
        subject = new LockGlobalRegionFunction(map);
    }

    @Test
    void lock() {

        String[] args = {LockGlobalRegionFunction.LOCK_ACTION};

        Set filter = Set.of(key);
        when(rfc.getFilter()).thenReturn(filter);
        when(rfc.getDataSet()).thenReturn(region);
        when(region.getDistributedLock(any())).thenReturn(lock);
        when(rfc.getArguments()).thenReturn(args);

        subject.execute(rfc);

        verify(lock).lock();
    }

    @Test
    void unlock() {

        String[] args = {LockGlobalRegionFunction.UNLOCK_ACTION};
        Set filter = Set.of(key);
        when(rfc.getFilter()).thenReturn(filter);
        when(rfc.getDataSet()).thenReturn(region);
        when(region.getDistributedLock(any())).thenReturn(lock);
        when(rfc.getArguments()).thenReturn(args);

        subject.execute(rfc);

        verify(lock).unlock();
    }
}
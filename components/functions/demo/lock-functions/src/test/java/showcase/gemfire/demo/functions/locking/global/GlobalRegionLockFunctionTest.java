package showcase.gemfire.demo.functions.locking.global;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalRegionLockFunctionTest {

    private GlobalRegionLockFunction subject;
    @Mock
    private FunctionContext<String[]> rfc;
    @Mock
    private Lock lock;

    @Mock
    private Region<Object, Object> region;

    private final Map<Object, Lock> map = new ConcurrentHashMap<>();
    private String key = "lock";
    @Mock
    private ResultSender sender;
    private String regionName = "region";
    @Mock
    private Cache cache;


    @BeforeEach
    void setUp() {
        subject = new GlobalRegionLockFunction(map);
    }

    @Test
    void lock() {

        String[] args = {GlobalRegionLockFunction.LOCK_ACTION,key,regionName};

        when(rfc.getCache()).thenReturn(cache);
        when(cache.getRegion(anyString())).thenReturn(region);
        when(region.getDistributedLock(any())).thenReturn(lock);
        when(rfc.getArguments()).thenReturn(args);
        when(rfc.getResultSender()).thenReturn(sender);

        subject.execute(rfc);

        verify(lock).lock();
        verify(sender).lastResult(any());
    }

    @Test
    void unlock() {

        String[] args = {GlobalRegionLockFunction.UNLOCK_ACTION,key,regionName};
        when(rfc.getCache()).thenReturn(cache);
        when(cache.getRegion(anyString())).thenReturn(region);
        when(region.getDistributedLock(any())).thenReturn(lock);
        when(rfc.getArguments()).thenReturn(args);
        when(rfc.getResultSender()).thenReturn(sender);

        subject.execute(rfc);

        verify(lock).unlock();
        verify(sender).lastResult(any());
    }

    @Test
    void lockl() throws InterruptedException {

        var semaphore = new Semaphore(1);

        semaphore.tryAcquire(1, TimeUnit.valueOf("MINUTES"));
        semaphore.tryAcquire(1, TimeUnit.valueOf("MINUTES"));
    }
}
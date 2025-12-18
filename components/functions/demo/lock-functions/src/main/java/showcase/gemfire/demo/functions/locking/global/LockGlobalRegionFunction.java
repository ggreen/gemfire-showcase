package showcase.gemfire.demo.functions.locking.global;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

public class LockGlobalRegionFunction implements Function<String[]> {
    public static final String LOCK_ACTION = "LOCK";
    public static final String UNLOCK_ACTION = "UNLOCK";
    private final Map<Object, Lock> lockMap;

    public LockGlobalRegionFunction()
    {
        this(new ConcurrentHashMap<>());
    }

    public LockGlobalRegionFunction(Map<Object, Lock> lockMap) {
        this.lockMap = lockMap;
    }

    @Override
    public void  execute(FunctionContext<String[]> functionContext) {
        var rfc = (RegionFunctionContext<String[]>)functionContext;

        var region = rfc.getDataSet();
        var entryKey = rfc.getFilter().iterator().next();

        var action = rfc.getArguments()[0];
        var lock = region.getDistributedLock(entryKey);

        switch (action)
        {
            case LOCK_ACTION -> {
                lock.lock();
                lockMap.put(entryKey,lock);
            }
            case UNLOCK_ACTION -> {
                lockMap.remove(entryKey);
                lock.unlock();
            }
            default -> throw new FunctionException("Unknown action:"+action);
        }
    }

    @Override
    public String getId() {
        return "LockGlobalRegion";
    }
}

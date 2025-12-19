package showcase.gemfire.demo.functions.locking.global;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

public class GlobalRegionLockFunction implements Function<String[]> {

    private Logger logger = LogManager.getLogger(GlobalRegionLockFunction.class);

    public static final String LOCK_ACTION = "LOCK";
    public static final String UNLOCK_ACTION = "UNLOCK";
    private final Map<Object, Lock> lockMap;

    public GlobalRegionLockFunction()
    {
        this(new ConcurrentHashMap<>());
    }

    public GlobalRegionLockFunction(Map<Object, Lock> lockMap) {
        this.lockMap = lockMap;
    }

    @Override
    public void  execute(FunctionContext<String[]> fc) {

        logger.info("Calling GlobalRegionLockFunction");

        var args = fc.getArguments();
        var action = args[0];
        var entryKey = args[1];
        var regionName = args[2];


        logger.info("action: {}, entryKey: {}, regionName: {}",action,
                entryKey,
                regionName,
                regionName);


        logger.info("Looking for key:{}, in map: {}",entryKey,lockMap);
        var lock = lockMap.get(entryKey);

        logger.info("Lock: {}",lock);

        if(lock == null)
        {
            logger.info("Getting region from region {}",regionName);
            lock = fc.getCache().getRegion(regionName).getDistributedLock(entryKey);
            logger.info("Lock from region: {}",lock);
        }

        switch (action)
        {
            case LOCK_ACTION -> {
                logger.info("Starting Lock");
                lock.lock();
                logger.info("GOT the Lock, Now put in map");
                lockMap.put(entryKey,lock);
                logger.info("Put lock in map");
            }
            case UNLOCK_ACTION -> {
                logger.info("Removing Lock from map");
                lockMap.remove(entryKey);
                logger.info("Unlocking");
                lock.unlock();
                logger.info("Unlocked");
            }
            default -> throw new FunctionException("Unknown action:"+action);
        }

        fc.getResultSender().lastResult(lock);

    }

    @Override
    public String getId() {
        return "GlobalRegionLockFunction";
    }
}

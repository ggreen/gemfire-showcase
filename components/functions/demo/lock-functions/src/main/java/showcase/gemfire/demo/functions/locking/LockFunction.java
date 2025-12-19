package showcase.gemfire.demo.functions.locking;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.distributed.DistributedLockService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import showcase.gemfire.demo.functions.locking.demo.LockingDemoFunction;
import showcase.gemfire.demo.functions.locking.domain.LockServiceContext;

import java.util.Set;

public class LockFunction implements Function<String[]> {
    private final java.util.function.Function<LockServiceContext, DistributedLockService> getLockService;
    private final Logger logger = LogManager.getLogger(LockingDemoFunction.class);

    public LockFunction()
    {
        this(context -> {
            var lockService =DistributedLockService.getServiceNamed(context.lockServiceName());
            if(lockService ==null)
                lockService = DistributedLockService.create(context.lockServiceName(),
                        context.cache().getDistributedSystem());

            return lockService;
        });
    }
    public LockFunction(java.util.function.Function<LockServiceContext, DistributedLockService> getLockService) {
        this.getLockService = getLockService;
    }

    @Override
    public void execute(FunctionContext functionContext) {

        logger.info("Calling lock services");

        var rfc = (RegionFunctionContext<String[]>) functionContext;
        var args = rfc.getArguments();

        Set<?> keySet = rfc.getFilter();
        Object lockKey = keySet.iterator().next();

        var lockServiceName = new LockServiceContext(args[0],rfc.getCache());
        var waitTime = Long.parseLong(args[1]);
        var leaseTime = Long.parseLong(args[2]);

        logger.info("lockServiceName: {}, Key: {} wait time: {}, leastTime: {} ",
                lockServiceName,
                lockKey,
                waitTime,leaseTime);

        var lockService = getLockService.apply(lockServiceName);

        logger.info("Getting lock");
        var wasLocked = lockService.lock(lockKey,waitTime,leaseTime);

        logger.info("Return lock wasLocked: {}",wasLocked);
        rfc.getResultSender().lastResult(wasLocked);

    }

    @Override
    public String getId() {
        return "LockFunction";
    }
}

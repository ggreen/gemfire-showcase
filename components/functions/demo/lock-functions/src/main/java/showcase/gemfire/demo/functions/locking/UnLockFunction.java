package showcase.gemfire.demo.functions.locking;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.distributed.DistributedLockService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import showcase.gemfire.demo.functions.locking.demo.LockingDemoFunction;
import showcase.gemfire.demo.functions.locking.domain.LockServiceContext;

public class UnLockFunction implements Function<String[]> {


    private final java.util.function.Function<LockServiceContext, DistributedLockService> getLockService;
    private final Logger logger = LogManager.getLogger(LockingDemoFunction.class);

    public UnLockFunction(java.util.function.Function<LockServiceContext, DistributedLockService> getLockService) {
        this.getLockService = getLockService;
    }

    public UnLockFunction()
    {
        this(context -> {
            var lockService =DistributedLockService.getServiceNamed(context.lockServiceName());
            if(lockService ==null)
                lockService = DistributedLockService.create(context.lockServiceName(),
                        context.cache().getDistributedSystem());

            return lockService;
        });
    }
    @Override
    public void execute(FunctionContext<String[]> functionContext) {

        logger.info("Unlocking with function");
        RegionFunctionContext<String[]> rfc = (RegionFunctionContext<String[]>)functionContext;

        var args = rfc.getArguments();
        var lockServiceName = new LockServiceContext(args[0], rfc.getCache());

        var keys = rfc.getFilter();
        var lockKey = keys.iterator().next();

        logger.info("Lock service: {}, keys: {}, lockKey: {}",
                lockServiceName,keys,lockKey);

        var lockService = getLockService.apply(lockServiceName);

        logger.info("Start unlock");
        lockService.unlock(lockKey);
        logger.info("End lock");

        rfc.getResultSender().lastResult(true);
    }
}

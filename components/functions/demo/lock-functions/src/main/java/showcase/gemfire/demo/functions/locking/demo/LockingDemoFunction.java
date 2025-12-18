package showcase.gemfire.demo.functions.locking.demo;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.distributed.DistributedLockService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockingDemoFunction implements Function {
    private Logger logger = LogManager.getLogger(LockingDemoFunction.class);

    @Override
    public void execute(FunctionContext functionContext) {

        logger.info("Calling lock services");

        RegionFunctionContext<String[]> rfc = (RegionFunctionContext<String[]>) functionContext;

        String[] args = rfc.getArguments();
        logger.info("args []", args);
        var serviceName = args[0];
        var lockName = args[1];
        long sleepMilliseconds = Long.parseLong(args[2]);

        Cache cache = rfc.getCache();

        logger.info("Creating lock service: {}", serviceName);
        var lockService =DistributedLockService.getServiceNamed(serviceName);
        if(lockService ==null)
                lockService = DistributedLockService.create(serviceName, cache.getDistributedSystem());
        logger.info("Got lock service: {}", serviceName);


        try {
            logger.info("Getting lock for key: {}", lockName);
            var lockAcquired = lockService.lock(lockName, -1, -1);
            logger.info("Got lock: {}, for key: {}", lockAcquired, lockName);

            logger.info("Start Sleeping for {} ms", sleepMilliseconds);
            Thread.sleep(sleepMilliseconds);

            rfc.getResultSender().lastResult(true);

        } catch (InterruptedException e) {
            logger.error(e);
            rfc.getResultSender().lastResult(false);
            throw new FunctionException(e);
        }
        finally {
            if (lockService.isHeldByCurrentThread(lockName)){
                try {
                    lockService.unlock(lockName);
                    logger.info("Unlocked: {}", lockName);
                } catch (Exception unlockEx) {
                    logger.warn("Failed to release lock", unlockEx);
                }
            }
        }
    }
}

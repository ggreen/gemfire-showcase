package showcase.gemfire.demo.functions.locking;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import showcase.gemfire.demo.functions.locking.demo.LockingDemoFunction;

import java.util.concurrent.Semaphore;

public class ReleaseSemaphoreFunction implements Function<String[]> {

    private final Logger logger = LogManager.getLogger(LockingDemoFunction.class);

    @Override
    public void execute(FunctionContext<String[]> functionContext) {

        logger.info("Unlocking with function");
        RegionFunctionContext<String[]> rfc = (RegionFunctionContext<String[]>)functionContext;

        var keys = rfc.getFilter();
        var lockKey = keys.iterator().next();

        logger.info("lockKey: {}",lockKey);
        Region<Object, Semaphore> region = rfc.getDataSet();

        var semaphore = region.get(lockKey);
        logger.info("Got semaphore: {}",semaphore);
        if(semaphore != null)
        {
            logger.info("Start release");
            semaphore.release();
            logger.info("End release");

            region.remove(lockKey);
            logger.info("Removed semaphore with key:{}",lockKey);
        }


        rfc.getResultSender().lastResult(true);
    }

    @Override
    public String getId() {
        return "ReleaseSemaphoreFunction";
    }
}

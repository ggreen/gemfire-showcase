package showcase.gemfire.demo.functions.locking;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import showcase.gemfire.demo.functions.locking.constants.LockingErrors;
import showcase.gemfire.demo.functions.locking.demo.LockingDemoFunction;

import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;

/**
 * This function works in conjunction with the AcquireSemaphoreFunction.
 * It will release a lock for a given filter key.
 * The function must be executed on a region. A partition region
 * with no persistence nor redundancy is the preferred region type.
 *
 * @author gregory green
 */
public class ReleaseSemaphoreFunction implements Function<String[]> {

    private final Logger logger = LogManager.getLogger(LockingDemoFunction.class);

    /**
     * Release the semaphore
     *
     * Filter key required
     * @param functionContext the function context
     */
    @Override
    public void execute(FunctionContext<String[]> functionContext) {

        logger.info("Unlocking with function");
        try
        {
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
        catch (NoSuchElementException e){
            throw new FunctionException(LockingErrors.FILTER_REQUIRED);
        }

    }

    /**
     *
     * @return true to always executed on the server with the primary bucket for a partitioned region
     */
    @Override
    public boolean optimizeForWrite() {
        return true;
    }

    @Override
    public String getId() {
        return "ReleaseSemaphoreFunction";
    }
}

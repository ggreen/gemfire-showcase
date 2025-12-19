package showcase.gemfire.demo.functions.locking;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import showcase.gemfire.demo.functions.locking.constants.LockingErrors;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * The Acquire semaphore function uses Java Semaphore.
 * The Semaphore will be acquired from a given lock with a given time out.
 * If another process has already acquired the semaphore for the given key,
 * the function will block based on the timeout.
 * The function must be executed on a region. A partition region
 * with no persistence nor redundancy is the preferred region type
 *  </pre>
 *
 * @author gregory green
 */
public class AcquireSemaphoreFunction implements Function<String[]> {
    private final Logger logger = LogManager.getLogger(AcquireSemaphoreFunction.class);
    private final java.util.function.Function<Integer,Semaphore> createSemaphore;

    public AcquireSemaphoreFunction(){
        this(Semaphore::new);
    }
    public AcquireSemaphoreFunction(java.util.function.Function<Integer, Semaphore> createSemaphore) {
        this.createSemaphore = createSemaphore;
    }

    /**
     * <pre>
     * This function must be executed on region.
     * the function contacts should have an array of strings
     *
     *    var permitText = args[0];
     *    var timeOut = Long.parseLong(args[1]);
     *    var timeUnit = args[2];
     *
     *    Note the possible Time Unit values are
     *     NANOSECONDS
     *     MICROSECONDS
     *     MILLISECONDS
     *     SECONDS
     *     MINUTES
     *     HOURS
     *     DAYS
     *   </pre>
     * @param functionContext the function context
     */
    @Override
    public void execute(FunctionContext functionContext) {

        logger.info("Calling lock services");

         try {

            var rfc = (RegionFunctionContext<String[]>) functionContext;
            var args = rfc.getArguments();
             Region<Object, Semaphore > region = rfc.getDataSet();

            Set<?> keySet = rfc.getFilter();
            Object lockKey = keySet.iterator().next();

             var permitText = args[0];
             var timeOut = Long.parseLong(args[1]);
             var timeUnit = args[2];

             logger.info("Lock Key:{}, timeout: {}, units:{}",lockKey,
                    timeOut,timeUnit);

             var semaphore = region.get(lockKey);
             if(semaphore == null) {

                int permit = Integer.parseInt(permitText);
                logger.info("Key not found in region, creating semaphore with permit: {}",permit);
                semaphore = createSemaphore.apply(permit);
                region.put(lockKey,semaphore);
                semaphore = region.get(lockKey);
            }

             boolean wasAcquired = semaphore.tryAcquire(timeOut, TimeUnit.valueOf(timeUnit));

             logger.info("Was semaphore acquired: {}",wasAcquired);

             rfc.getResultSender().lastResult(wasAcquired);

         }
         catch (NullPointerException e){
             throw new FunctionException(LockingErrors.ARGUMENTS_REQUIRED);
         }
         catch (NoSuchElementException e){
             throw new FunctionException(LockingErrors.FILTER_REQUIRED);
         }
         catch (InterruptedException | RuntimeException e) {
             logger.error(e);
             throw new FunctionException(e);
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
        return "AcquireSemaphoreFunction";
    }
}

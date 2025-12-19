package showcase.gemfire.demo.functions.locking;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import showcase.gemfire.demo.functions.locking.demo.LockingDemoFunction;

import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author gregory green
 */
public class AcquireSemaphoreFunction implements Function<String[]> {
    private final Logger logger = LogManager.getLogger(LockingDemoFunction.class);
    private final java.util.function.Function<Integer,Semaphore> createSemaphore;

    public AcquireSemaphoreFunction(){
        this(Semaphore::new);
    }
    public AcquireSemaphoreFunction(java.util.function.Function<Integer, Semaphore> createSemaphore) {
        this.createSemaphore = createSemaphore;
    }

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

         } catch (InterruptedException | RuntimeException e) {
             logger.error(e);
             throw new FunctionException(e);
         }
    }

    @Override
    public boolean optimizeForWrite() {
        return true;
    }

    @Override
    public String getId() {
        return "AcquireSemaphoreFunction";
    }
}

package com.vmware.data.services.gemfire.operations.functions.touch;

import org.apache.geode.CopyHelper;
import org.apache.geode.cache.*;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;


/**
 *
 *
 * <pre>
 Testing

 ```shell
 start locator --name=c1-locator --port=10000 --enable-cluster-configuration=true --J=-Dgemfire.jmx-manager-port=10100  --J=-Dgemfire.remote-locators=localhost[20000] --J=-Dgemfire.distributed-system-id=1
 ```

 ```shell
 start server --name=c1-server1 --locators=localhost[10000] --server-port=40001
 ```


 ```shell
 connect --locator=localhost[10000]
 ```

 ```shell
 create gateway-receiver --start-port=50101 --end-port=50110
 ```

 ```shell
 create gateway-sender --id=sender-1-to-2 --remote-distributed-system-id=2 --parallel=true
 ```

 ```shell
 create region --name=test-gw --type=PARTITION_REDUNDANT --gateway-sender-id=sender-1-to-2
 ```


 ## Set Up Cluster 2


 ```shell
 disconnect
 ```

 ```shell
 start locator --name=c2-locator --port=20000 --enable-cluster-configuration=true --J=-Dgemfire.http-service-port=7020 --J=-Dgemfire.jmx-manager-port=20100 --J=-Dgemfire.remote-locators=localhost[10000]  --J=-Dgemfire.distributed-system-id=2
 ```

 ```shell
 start server --name=c2-server1 --locators=localhost[20000] --server-port=50001 --use-cluster-configuration=true
 ```

 ```shell
 connect --locator=localhost[20000]
 ```

 ```shell
 create gateway-receiver --start-port=50201 --end-port=50210
 ```

 ```shell
 create gateway-sender --id=sender-2-to-1 --remote-distributed-system-id=1 --parallel=true
 ```

 ```shell
 create region --name=test-gw --type=PARTITION_REDUNDANT --gateway-sender-id=sender-2-to-1
 ```

 ----------------
 ## Testing


 ```shell
 disconnect
 connect --locator=localhost[20000]
 ```

 ```shell
 put --region=/test-gw --key=2 --value=2
 ```

 ```shell
 disconnect
 connect --locator=localhost[10000]
 deploy --jar=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/apache-geode-extensions/components/legacy-gemfire-8-2/legacy-gemtools/gemtoolsServer/target/gemtoolsServer-0.0.1-SNAPSHOT.jar
 ```

 ```shell
 put --region=/test-gw --key=1 --value=1
 ```

 ```shell
 put --region=/test-gw --key=2 --value=2
 ```

 ```shell
 disconnect
 connect --locator=localhost[20000]
 ```


 ```shell
 query --query="select * from /test-gw"
 ```

 ```shell
 remove --region=/test-gw --key=1
 remove --region=/test-gw --key=2
 ```


 ```shell
 execute function --id=Touch --region=/test-gw
 ```
 </pre>
 *
 * @author Randy May and Gregory Green
 *
 */

public class TouchFunction implements Function, Declarable {

    /**
     * Report to log report rate per second property
     */
    public static final String TOUCH_REPORT_INTERVAL_MS_PROP = "touchReportIntervalMs";

    /**
     * The property name to control how many touches are done per second
     */
    public static final String TOUCH_TARGET_RATE_PER_SEC_FLOW_CONTROL_PROP = "touchTargetRatePerSecFlowControl";

    /**
     * The property name to control how many touches are done for each batch transaction
     */
    public static final String TOUCH_BATCH_SIZE_PROP = "touchBatchSize";

    /**
     * Error message when not executed on Region
     */
    private final static String notRegionFunctionContextError  ="\"TouchFunction must be executed on a region. Ex: execute function --id=TouchFunction --region=/myRegion";

    private static final long serialVersionUID = 8827164389473146995L;
    private final long reportIntervalMs;
    private final long targetRatePerSecFlowControl;
    private final int batchSize;

    private final Supplier<CacheTransactionManager> txtMgrSupplier;
    private final Logger logger;
    private final java.util.function.Function<RegionFunctionContext, Region<Object,Object>> regionGetter;
    private final boolean copyOnRead;

    /**
     * Default constructor
     */
    public TouchFunction()
    {
        this(
                LogManager.getLogger(TouchFunction.class),
                (regionFunctionContext) -> PartitionRegionHelper.getLocalDataForContext(regionFunctionContext),
                () -> CacheFactory.getAnyInstance().getCacheTransactionManager(),
                CacheFactory.getAnyInstance().getCopyOnRead(),
                getConfigLong(TOUCH_REPORT_INTERVAL_MS_PROP,10L * 1000L),
                getConfigLong(TOUCH_TARGET_RATE_PER_SEC_FLOW_CONTROL_PROP,10),
                getConfigInt(TOUCH_BATCH_SIZE_PROP,10)
                );
    }

    /**
     * Creates the touch function instance
     * @param logger the log4j logger
     * @param regionFunctionContextSupplier the supplier that provides the region
     * @param txtMgrSupplier the supplier that provides the transaction manager
     * @param copyOnRead determine is copy on read is used
     * @param reportIntervalMs determines how often report to log is executed
     * @param targetRatePerSecFlowControl controls how many touches are done per second
     * @param batchSize controls how many touches are done per transcation.
     */
    protected TouchFunction(Logger logger,
                         java.util.function.Function<RegionFunctionContext,
                                 Region<Object,Object>> regionFunctionContextSupplier,
                         Supplier<CacheTransactionManager> txtMgrSupplier,
                         boolean copyOnRead,
                         long reportIntervalMs,
                        long targetRatePerSecFlowControl,
                        int batchSize)
    {
        this.reportIntervalMs = reportIntervalMs;
        this.targetRatePerSecFlowControl = targetRatePerSecFlowControl;
        this.batchSize = batchSize;
        this.logger = logger;
        this.regionGetter = regionFunctionContextSupplier;
        this.txtMgrSupplier = txtMgrSupplier;
        this.copyOnRead = copyOnRead;
    }


    @Override
    public void execute(FunctionContext ctx) {
        try
        {
            if(!(ctx instanceof RegionFunctionContext))
                throw new FunctionException(notRegionFunctionContextError);

            var regionFunctionContext = (RegionFunctionContext) ctx;
            Region<Object,Object> region = regionFunctionContext.getDataSet();
            if (region.getAttributes().getDataPolicy().withPartitioning() ){
                region = regionGetter.apply(regionFunctionContext);
            }

            Set<?> filter = regionFunctionContext.getFilter();

            if (filter == null || filter.isEmpty())
                filter = region.keySet();

            Object []keys = new Object[filter.size()];
            filter.toArray(keys);

            int i=0;
            var invocation = new Invocation(region.getFullPath(), keys.length);

            for(; i+ batchSize < keys.length; i+= batchSize){
                processBatch(invocation,  region, Arrays.copyOfRange(keys,i,i+ batchSize));
            }
            // left over batch
            if (i <keys.length){
                processBatch(invocation,  region,Arrays.copyOfRange(keys,i,keys.length));
            }

            invocation.lastReport();
        }
        catch(FunctionException e)
        {
            this.logger.error(stackTrace(e));
            throw e;
        }
        catch (Exception e)
        {
            this.logger.error(stackTrace(e));

            throw new FunctionException(e);
        }
    }

    /**
     * Performance a touch of the region for the provided keys
     * @param invocation strategy to perform the execution
     * @param region the region to touch
     * @param keys the batch of keys
     */
    private void processBatch(Invocation invocation, Region<Object,Object> region, Object[]keys){
        // introduce sleep as necessary to throttle to the desired rate

        if ( targetRatePerSecFlowControl > 0){
            long currentRate = invocation.getTouchesPerSecond();
            if (currentRate > targetRatePerSecFlowControl){
                long targetElapsedMs = (invocation.getTouched() * 1000) / targetRatePerSecFlowControl;
                long sleep = targetElapsedMs - invocation.getElapsedMs();
                if (sleep > 0){
                    try {
                        Thread.sleep(sleep);
                    } catch(InterruptedException x){
                        // not a problem
                    }
                }
            }
        }


        // do the touch using transaction semantics so we will not accidentally
        // undo an update that is happening concurrently
        var tm = txtMgrSupplier.get();
        tm.begin();
        try {
            for(Object key: keys) putGet(region,key, !copyOnRead);
            tm.commit();
            tm = null;
        } catch(CommitConflictException x){
            processBatchOneAtATime(region, keys);
        } finally {
            if (tm != null) tm.rollback();
            tm = null;
        }
        invocation.incrementTouched(keys.length);

        // now assess whether we need to send back a status report / log a message
        if (invocation.getTimeSinceLastReport() > reportIntervalMs) invocation.report();
    }

    /**
     * Perform a touch for keys that previous had a commit
     * @param region the region to touch
     * @param keys the touch
     */
    private void processBatchOneAtATime(Region<Object,Object> region, Object[]keys){
        // do the touch using transaction semantics so we will not accidentally
        // undo an update that is happening concurrently
        for(Object key : keys){
            var tm = txtMgrSupplier.get();
            tm.begin();
            try {
                putGet(region, key, !copyOnRead);
                tm.commit();
                tm = null;
            } catch(CommitConflictException x){
                // this is OK - it just means someone else updated the key and we don't want to overwrite it
                logger.info("Note: received a conflict while updating key: %",key," so it will not be overwritten");
            } finally {
                if (tm != null) tm.rollback();
                tm = null;
            }
        }
    }

    /**
     * Performance a get and put of a region entry
     * @param region the region to update
     * @param key the key to update
     * @param copy indicate if a copy is needed
     */
    private void putGet(Region<Object,Object> region, Object key, boolean copy){
        var val = region.get(key);

        if (val != null){
            if (copy){
                key = CopyHelper.copy(key);
                val = CopyHelper.copy(val);
            }

            region.put(key, val);
        }
    }

    /**
     *
     * @return "Touch";
     */
    @Override
    public String getId() {
        return "TouchFunction";
    }

    /**
     *
     * @return false;
     */
    @Override
    public boolean hasResult() {
        return false;
    }

    /**
     *
     * @return true
     */
    @Override
    public boolean isHA() {
        return true;
    }

    /**
     *
     * @return true
     */
    @Override
    public boolean optimizeForWrite() {
        return true;
    }


    /**
     * All invocation related state will be stored in a newly allocated instance of Invocation.
     */
    private class Invocation {
        private String regionName;
        private long totalEntries;
        private long touched;
        private long startTime;
        private long lastReport;

        public Invocation(String regionName, long totalEntries){
            this.regionName = regionName;
            this.totalEntries = totalEntries;
            touched = 0L;
            lastReport = 0L;
            startTime = System.currentTimeMillis();
        }

        public void incrementTouched(long i){
            touched += i;
        }

        public long getTouched(){
            return touched;
        }

        public long getElapsedMs(){
            return System.currentTimeMillis() - startTime;
        }

        public long getTouchesPerSecond(){
            long elapsed = this.getElapsedMs();
            if (elapsed == 0) return 0; // avoids divide by zero

            return (this.getTouched() * 1000) / elapsed;
        }

        public long getTimeSinceLastReport(){
            return System.currentTimeMillis() - lastReport;
        }

        public void report(){
            var msg = "touched " + touched + "/" + totalEntries + " entries in " + regionName;
            lastReport = System.currentTimeMillis();
            logger.info(msg);
        }

        public void lastReport(){
            var msg = "FINISHED: touched " + touched + "/" + totalEntries + " entries in " + regionName;
            lastReport = System.currentTimeMillis();
            logger.info(msg);
        }
    }

    @Override
    public void init(Properties properties)
    {
    }

    String stackTrace(Throwable t)
    {
        var writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    static int getConfigInt(String propertyName, int defaultValue) {
        var value = System.getProperty(propertyName);

        if(value == null || value.length() ==0 )
            return defaultValue;
        return Integer.valueOf(value);
    }

    static long getConfigLong(String propertyName, long defaultValue) {
        var value = System.getProperty(propertyName);

        if(value == null || value.length() ==0 )
            return defaultValue;
        return Long.valueOf(value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TouchFunction{");
        sb.append("reportIntervalMs=").append(reportIntervalMs);
        sb.append(", targetRate=").append(targetRatePerSecFlowControl);
        sb.append(", batchSize=").append(batchSize);
        sb.append(", txtMgrSupplier=").append(txtMgrSupplier);
        sb.append(", logger=").append(logger);
        sb.append(", regionGetter=").append(regionGetter);
        sb.append(", copyOnRead=").append(copyOnRead);
        sb.append('}');
        return sb.toString();
    }
}

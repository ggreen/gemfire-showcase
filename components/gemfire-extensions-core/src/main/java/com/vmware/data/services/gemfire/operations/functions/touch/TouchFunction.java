package com.vmware.data.services.gemfire.operations.functions.touch;

import org.apache.geode.CopyHelper;
import org.apache.geode.LogWriter;
import org.apache.geode.cache.*;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.partition.PartitionRegionHelper;

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

    private static final long serialVersionUID = 8827164389473146995L;
    private static long REPORT_INTERVAL_MS = 10L * 1000L;
    private final Supplier<CacheTransactionManager> txtMgrSupplier;
    private long targetRate = 10;
    private int batchSize = 100;

    private transient final LogWriter logger;
    private final java.util.function.Function<RegionFunctionContext, Region<Object,Object>> regionGetter;
    private final boolean copyOnRead;

    /**
     * Default constructor
     */
    public TouchFunction()
    {
        this(
                CacheFactory.getAnyInstance().getLogger(),
                (regionFunctionContext) -> PartitionRegionHelper.getLocalDataForContext(regionFunctionContext),
                () -> CacheFactory.getAnyInstance().getCacheTransactionManager(),
                CacheFactory.getAnyInstance().getCopyOnRead()
                );
    }

    public TouchFunction(LogWriter logger,
                         java.util.function.Function<RegionFunctionContext,
                                 Region<Object,Object>> supplier,
                         Supplier<CacheTransactionManager> txtMgrSupplier,
                         boolean copyOnRead)
    {
        this.logger = logger;
        this.regionGetter = supplier;
        this.txtMgrSupplier = txtMgrSupplier;
        this.copyOnRead = copyOnRead;
    }


    @Override
    public void execute(FunctionContext ctx) {
        try
        {
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
                processBatch(invocation,  region, Arrays.copyOfRange(keys,i,i+ batchSize), regionFunctionContext.<String>getResultSender());
            }
            // left over batch
            if (i <keys.length){
                processBatch(invocation,  region,Arrays.copyOfRange(keys,i,keys.length), regionFunctionContext.<String>getResultSender());
            }

            invocation.lastReport( regionFunctionContext.<String>getResultSender());
        }
        catch (RuntimeException e)
        {
            this.logger.error(stackTrace(e));

            throw e;
        }
        catch (Exception e)
        {
            this.logger.error(stackTrace(e));

            throw new RuntimeException(e);
        }
    }

    private void processBatch(Invocation invocation, Region<Object,Object> region, Object[]keys, ResultSender<String> resultSender){
        // introduce sleep as necessary to throttle to the desired rate

        if ( targetRate > 0){
            long currentRate = invocation.getTouchesPerSecond();
            if (currentRate > targetRate){
                long targetElapsedMs = (invocation.getTouched() * 1000) / targetRate;
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
        if (invocation.getTimeSinceLastReport() > REPORT_INTERVAL_MS) invocation.report(resultSender);
    }

    private void processBatchOneAtATime(Region<Object,Object> region, Object[]keys){
        // do the touch using transaction semantics so we will not accidentally
        // undo an update that is happening concurrently
        for(Object key : keys){
            CacheTransactionManager tm = txtMgrSupplier.get();
            tm.begin();
            try {
                putGet(region, key, !copyOnRead);
                tm.commit();
                tm = null;
            } catch(CommitConflictException x){
                // this is OK - it just means someone else updated the key and we don't want to overwrite it
            } finally {
                if (tm != null) tm.rollback();
                tm = null;
            }
        }
    }

    private void putGet(Region<Object,Object> region, Object key, boolean copy){
        Object val = region.get(key);

        if (val != null){
            if (copy){
                key = CopyHelper.copy(key);
                val = CopyHelper.copy(val);
            }

            region.put(key, val);
        }
    }

    @Override
    public String getId() {
        return "Touch";
    }

    @Override
    public boolean hasResult() {
        return false;
    }

    @Override
    public boolean isHA() {
        return true;
    }

    @Override
    public boolean optimizeForWrite() {
        return true;
    }

    // I'm not certain whether / how Function instances are re-uses within the server so
    // all invocation related state will be stored in a newly allocated instance of Invocation.
    // Since there will be one instance per invocation it does not need to be thread safe.
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

        public void report(ResultSender<String> resultSender){
            var msg = "touched " + touched + "/" + totalEntries + " entries in " + regionName;
            logger.info(msg);
            lastReport = System.currentTimeMillis();
            resultSender.sendResult(msg);
        }

        public void lastReport(ResultSender<String> resultSender){
            String msg = "FINISHED: touched " + touched + "/" + totalEntries + " entries in " + regionName;
            logger.info(msg);
            lastReport = System.currentTimeMillis();
            resultSender.lastResult(msg);
        }
    }

    @Override
    public void init(Properties properties)
    {
    }

    private static String stackTrace(Throwable t)
    {
        var sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
}

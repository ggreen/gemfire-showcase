package com.vmware.data.services.gemfire.operations.functions;

import com.vmware.data.services.gemfire.operations.functions.strategy.GetRegion;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

import static org.apache.geode.cache.wan.GatewaySender.DEFAULT_BATCH_SIZE;


/**
 * Clears a given region data
 */
public class ClearRegionRemoveAllFunction implements Function, Declarable {

    private Logger logger  = LogManager.getLogger(ClearRegionRemoveAllFunction.class);
    private final java.util.function.Function<FunctionContext<?>,Region<Object, Object>> getRegion;

    public ClearRegionRemoveAllFunction()
    {
        this(new GetRegion());
    }

    public ClearRegionRemoveAllFunction(
            java.util.function.Function<FunctionContext<?>,Region<Object, Object>>
                    getRegion) {
        this.getRegion = getRegion;
    }

    @Override
    public void execute(FunctionContext functionContext) {
        int batchSize = DEFAULT_BATCH_SIZE;
        String[] arguments = (String[]) functionContext.getArguments();
        if(arguments != null)
            batchSize = Integer.parseInt(arguments[0]);

        Region<Object, Object> region = getRegion.apply(functionContext);

        // Destroy each entry using removeAll
        logger.info("About to clear region name=" + region.getName() + "; size=" + region.size() + "; batchSize=" + batchSize);

        long start=System.currentTimeMillis();
        long  end=0;

        Set<Object> keysToClear = new HashSet<Object>();
        Set<Object> keySet = region.keySet();
        int keyCount = keySet.size();

        int i = 0;
        for (Object key : keySet) {
            keysToClear.add(key);
            if ((i+1) % batchSize == 0) {
                region.removeAll(keysToClear);
                keysToClear.clear();
            }
            i++;
        }

        if(!keysToClear.isEmpty())
            region.removeAll(keysToClear);


        end = System.currentTimeMillis();
        logger.info("Cleared region name={}; count={} size={} in {} ms",keyCount, region.getName(), region.size(), end - start);

        functionContext.getResultSender().lastResult(keyCount);
    }

    @Override
    public String getId() {
        return "ClearRegionRemoveAllFunction";
    }
}

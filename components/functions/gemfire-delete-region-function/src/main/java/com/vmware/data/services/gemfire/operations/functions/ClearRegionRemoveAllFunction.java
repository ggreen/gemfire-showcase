package com.vmware.data.services.gemfire.operations.functions;

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


public class ClearRegionRemoveAllFunction implements Function, Declarable {


    private Logger logger  = LogManager.getLogger(ClearRegionRemoveAllFunction.class);

    @Override
    public void execute(FunctionContext functionContext) {
        RegionFunctionContext rfc = (RegionFunctionContext) functionContext;
        int batchSize = DEFAULT_BATCH_SIZE;
        String[] arguments = (String[]) rfc.getArguments();
        if(arguments != null)
            batchSize = Integer.parseInt(arguments[0]);

        Region<Object,Object> region = rfc.getDataSet();

        // Destroy each entry using removeAll
        logger.info("About to clear region name=" + region.getName() + "; size=" + region.size() + "; batchSize=" + batchSize);

        long start=System.currentTimeMillis();
        long  end=0;

        Set<Object> keysToClear = new HashSet<Object>();
        int i = 0;
        for (Object key : region.keySet()) {
            keysToClear.add(key);
            if ((i+1) % batchSize == 0) {
                region.removeAll(keysToClear);
                keysToClear.clear();
            }
            i++;
        }

        region.removeAll(keysToClear);
        end = System.currentTimeMillis();
        logger.info("Cleared region name=" + region.getName() + "; size=" + region.size() + " in " + (end-start) + " ms");

        rfc.getResultSender().lastResult(true);
    }

    @Override
    public String getId() {
        return "ClearRegionRemoveAllFunction";
    }
}

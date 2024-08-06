package com.vmware.data.services.gemfire.operations.functions;

import com.vmware.data.services.gemfire.operations.functions.strategies.GetArgs;
import com.vmware.data.services.gemfire.operations.functions.strategies.GetRegionFromFunctionContext;
import com.vmware.data.services.gemfire.operations.functions.strategies.QueryFromFunctionContext;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Delete record must on query results of keys
 * @author gregory green
 */
public class DeleteFunction implements Function {

    private  final java.util.function.Function<FunctionContext, Collection<Object>> getQueryService;
    private final java.util.function.Function<FunctionContext, Region<Object,Object>> getRegion;
    private Logger logger = LogManager.getLogger(GetRegionFromFunctionContext.class);
    private GetArgs getArgs = new GetArgs();

    /**
     * Default constructor with strategies
     * GetRegionFromFunctionContext & QueryFromFunctionContext
     */
    public DeleteFunction()
    {
        this.getQueryService = new QueryFromFunctionContext();
        this.getRegion = new GetRegionFromFunctionContext();

    }

    public DeleteFunction(java.util.function.Function<FunctionContext, Region<Object, Object>> getRegion, java.util.function.Function<FunctionContext, Collection<Object>> getResults) {
        this.getRegion = getRegion;
        this.getQueryService= getResults;
    }


    @Override
    public void execute(FunctionContext functionContext) {

        Region<Object,Object> region = this.getRegion.apply(functionContext);

        Collection<Object> keys = this.getQueryService.apply(functionContext);


        // Destroy each entry using removeAll
        int batchSize = getArgs.getBatchSize(functionContext);

        logger.info("About to delete  from region={}, keys.size()={}, batchSize={}", region.getName(), keys.size(),batchSize);


        Set<Object> keysToClear = new HashSet<Object>(batchSize);
        int i = 0;
        for (Object key : keys) {
            keysToClear.add(key);
            if ((i+1) % batchSize == 0) {
                region.removeAll(keysToClear);
                keysToClear.clear();
            }
            i++;
        }

        if(!keysToClear.isEmpty())
        {
            region.removeAll(keysToClear);
        }

        functionContext.getResultSender().lastResult(keys.size());
    }

    @Override
    public String getId() {
        return "DeleteFunction";
    }

}

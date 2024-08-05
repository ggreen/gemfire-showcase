package com.vmware.data.services.gemfire.operations.functions.strategies;


import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;
import java.util.function.Supplier;

public class GetRegionFromFunctionContext implements Function<FunctionContext, Region<Object,Object>> {

    private final Supplier<Cache> cacheSupplier;
    private Logger logger = LogManager.getLogger(GetRegionFromFunctionContext.class);

    public GetRegionFromFunctionContext()
    {
        this(() -> CacheFactory.getAnyInstance());
    }

    public GetRegionFromFunctionContext(Supplier<Cache> cacheSupplier) {
        this.cacheSupplier = cacheSupplier;
    }

    @Override
    public Region<Object, Object> apply(FunctionContext functionContext) {

        if(functionContext instanceof RegionFunctionContext)
            return ((RegionFunctionContext)functionContext).getDataSet();
        else{
            String args[] = (String[]) functionContext.getArguments();
            if(args == null)
                throw new FunctionException("Region name in args required");

            logger.info("args; {}", args);
            return cacheSupplier.get().getRegion(args[0]);
        }

    }
}

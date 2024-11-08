package com.vmware.data.services.gemfire.operations.functions.strategies;


import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;

import java.util.function.Function;
import java.util.function.Supplier;

public class GetRegionFromFunctionContext implements Function<FunctionContext, Region<Object,Object>> {

    private final Supplier<Cache> cacheSupplier;

    public GetRegionFromFunctionContext()
    {
        this(() -> CacheFactory.getAnyInstance());
    }

    public GetRegionFromFunctionContext(Supplier<Cache> cacheSupplier) {
        this.cacheSupplier = cacheSupplier;
    }

    @Override
    public Region<Object, Object> apply(FunctionContext functionContext) {

        if(!(functionContext instanceof RegionFunctionContext))
            throw  new FunctionException("Function must be executed on a region");

        //local data
        return ((RegionFunctionContext)functionContext).getDataSet();

    }
}

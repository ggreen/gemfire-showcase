package com.vmware.data.services.gemfire.operations.functions.strategy;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;

import java.util.function.Function;

public class GetRegion implements Function<FunctionContext<?>,Region<Object,Object>> {


    @Override
    public Region<Object, Object> apply(FunctionContext<?> functionContext) {

        if(!(functionContext instanceof RegionFunctionContext))
            throw new FunctionException("Execute this function onRegion");

        RegionFunctionContext<?> rfc = (RegionFunctionContext<?>) functionContext;

        return PartitionRegionHelper.getLocalData(rfc.getDataSet());
    }
}

package com.vmware.data.services.gemfire.operations.functions;

import com.vmware.data.services.gemfire.operations.functions.strategies.GetRegionFromFunctionContext;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.*;

import java.util.Collection;

public class DeleteFunction implements Function {

    private  final java.util.function.Function<FunctionContext,Region<Object,Object>> getRegion;
    private  final java.util.function.Function<FunctionContext, Collection<Object>> getQueryService;

    public DeleteFunction(java.util.function.Function<FunctionContext, QueryService> getQueryService)
    {
        this(new GetRegionFromFunctionContext(),null);
    }
    public DeleteFunction(java.util.function.Function<FunctionContext, Region<Object, Object>> getRegion, java.util.function.Function<FunctionContext, Collection<Object>> getQueryService) {
        this.getRegion = getRegion;
        this.getQueryService = getQueryService;
    }

    @Override
    public void execute(FunctionContext functionContext) {

        Region<Object,Object> region = getRegion.apply(functionContext);
        String[] args = (String[]) functionContext.getArguments();

        String oql = args[0];

        Collection<Object> keys = this.getQueryService.apply(functionContext);
        region.removeAll(keys);

    }



}

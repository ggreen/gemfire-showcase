package com.vmware.data.services.gemfire.operations.functions.strategies;

import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.*;

import java.util.Collection;
import java.util.function.Function;

public class QueryFromFunctionContext implements Function<FunctionContext, Collection<Object>> {


    @Override
    public Collection<Object> apply(FunctionContext functionContext) {
        QueryService queryService = getQueryService(functionContext);

        String oql = getOql(functionContext);
        try {
            if(functionContext instanceof RegionFunctionContext)
                return (Collection<Object>) queryService.newQuery(oql).execute((RegionFunctionContext)functionContext);
            else
            return (Collection<Object>) queryService.newQuery(oql).execute();

        } catch (Exception e) {
            throw new FunctionException(e);
        }
    }

    private String getOql(FunctionContext functionContext) {
        return ((String[])functionContext.getArguments())[0];
    }

    private QueryService getQueryService(FunctionContext functionContext) {
        return ((RegionFunctionContext)functionContext).getCache().getQueryService();

    }
}

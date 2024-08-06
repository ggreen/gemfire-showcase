package com.vmware.data.services.gemfire.operations.functions.strategies;

import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.QueryService;

import java.util.Collection;
import java.util.function.Function;

public class QueryFromFunctionContext implements Function<FunctionContext, Collection<Object>> {


    @Override
    public Collection<Object> apply(FunctionContext functionContext) {
        QueryService queryService = functionContext.getCache().getQueryService();

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

        Object args = functionContext.getArguments();
        if(!(args instanceof  String[]))
            throw new FunctionException("Function arguments type must be String[]");

        String[] arguments = (String[])args;

        if(arguments.length == 0)
            throw new FunctionException("Function arguments type must be String[]");
        return arguments[0];
    }
}

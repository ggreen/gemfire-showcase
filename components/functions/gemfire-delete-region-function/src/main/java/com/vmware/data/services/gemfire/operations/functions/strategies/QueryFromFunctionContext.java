package com.vmware.data.services.gemfire.operations.functions.strategies;

import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.QueryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.function.Function;

public class QueryFromFunctionContext implements Function<FunctionContext, Collection<Object>> {

    private final GetArgs getArgs = new GetArgs();
    private Logger logger = LogManager.getLogger(QueryFromFunctionContext.class);

    @Override
    public Collection<Object> apply(FunctionContext functionContext) {
        QueryService queryService = functionContext.getCache().getQueryService();

        String oql = getArgs.getOql(functionContext);
        logger.info("OQL: {}",oql);

        try {
            if(functionContext instanceof RegionFunctionContext)
                return (Collection<Object>) queryService.newQuery(oql).execute((RegionFunctionContext)functionContext);
            else
            return (Collection<Object>) queryService.newQuery(oql).execute();

        } catch (Exception e) {
            throw new FunctionException("ERROR executing orl:"+oql+" "+e,e);
        }
    }

}

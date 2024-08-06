package com.vmware.data.services.gemfire.operations.functions.strategies;

import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.apache.geode.cache.wan.GatewaySender.DEFAULT_BATCH_SIZE;

/**
 * Stategy object to retrieve input arguments such as OQL and Batch Size
 *
 * @author gregory green
 */
public class GetArgs {

    private final OqlFormatter oqlFormatter = new OqlFormatter();
    private Logger logger = LogManager.getLogger(GetArgs.class);

     public String getOql(FunctionContext functionContext) {

        String[] arguments = getStringArgs(functionContext);

        if(arguments.length == 0)
            throw new FunctionException("Function arguments type must be String[]");
        String inputOql = arguments[0];
        logger.info("INPUT raw oql: {}",inputOql);

        return oqlFormatter.format(inputOql);
    }
    private String [] getStringArgs(FunctionContext functionContext)
    {
        Object args = functionContext.getArguments();
        if(!(args instanceof  String[]))
            throw new FunctionException("Function arguments type must be String[]");

        return (String[]) args;
    }

    public int getBatchSize(FunctionContext functionContext) {

        String[] arguments = getStringArgs(functionContext);
        int batchSize = DEFAULT_BATCH_SIZE;

        if(arguments.length > 1)
        {
            batchSize = Integer.parseInt(arguments[1]);
        }

        return batchSize;
    }
}

package showcase.gemfire.demo.functions;

import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static java.util.Arrays.asList;


public class WriteOpOnRegionFunction implements Function<Object[]>, Declarable {

    private final Logger logger  = LogManager.getLogger(WriteOpOnRegionFunction.class);

    @Override
    public void execute(FunctionContext<Object[]> functionContext) {

        var rfc = (RegionFunctionContext<Object[]>)functionContext;
        var distributeSystem = functionContext.getCache().getDistributedSystem();
        var distributedMember = distributeSystem.getDistributedMember();

        logger.info("distributeSystem: {}",distributeSystem);
        logger.info("rfc.args: {}",asList(rfc.getArguments()));
        logger.info("rfc.filter: {}",rfc.getFilter());

        functionContext.getResultSender()
                .lastResult(
                        Map.of(
                                "distributeSystemName",distributeSystem.getName(),
                                "distributedMemberId",distributedMember.getId(),
                                "distributedMemberName",distributedMember.getName()));


    }

    @Override
    public boolean optimizeForWrite()
    {
        return true;
    }

    @Override
    public String getId() {
        return getClass().getSimpleName();
    }
}

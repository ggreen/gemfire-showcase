package showcase.gemfire.balancer;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;


/**
 * This function will balance partitioned region data is needed.
 * A GemFire re-balance operation can be expensive. This function to check if there is a minimum
 * number of the members. It will also simulate the re-balance operation before actually performing the rebalance opertation.
 *
 * @author gregory green
 */
public class BalancerFunction implements Function, Declarable {

    private Logger logger  = LogManager.getLogger(BalancerFunction.class);
    private final Supplier<Cache> cacheSupplier;

    /**
     * Default Constructor used when deploying function with
     * Gfsh
     */
    public BalancerFunction()
    {
        this(CacheFactory::getAnyInstance);
    }

    /**
     * Constructor for the balancer
     * @param cacheSupplier the supplier for get cache (convenient for QQ verification)
     */
    public BalancerFunction(Supplier<Cache> cacheSupplier) {
        this.cacheSupplier = cacheSupplier;
    }


    /**
     * Rebalance data if needed
     * @param functionContext the function context
     */
    @Override
    public void execute(FunctionContext functionContext) {

        Cache cache = cacheSupplier.get();
        var resourceManager = cache.getResourceManager();
        var sender = functionContext.getResultSender();

        try {

            //If minCacheServerCount arguments, then only execute if cache servers > min count
            //This will prevent out of memory issues
            String[] arguments = (String[]) functionContext.getArguments();
            if(arguments != null && arguments.length > 0)
            {
                int minCacheServerCount = Integer.parseInt(arguments[0]);
                var cacheServers = cache.getCacheServers();

                if(cacheServers == null || cacheServers.size() < minCacheServerCount)
                {
                    logger.warn("NO re-balance was performed because the server count:{} is less than the requested count: {} ",cacheServers.size(),minCacheServerCount);
                    sender.lastResult(0);
                    return;
                }
            }

            var  rebalanceFactory = resourceManager.createRebalanceFactory();


            var ops = rebalanceFactory.start();
            var results = ops.getResults();

            logger.info("results: {}",results);
            sender.lastResult(results.getTotalMembersExecutedOn());

        } catch (InterruptedException e) {
            throw new FunctionException(e);
        }
    }

    @Override
    public String getId() {
        return "BalancerFunction";
    }
}

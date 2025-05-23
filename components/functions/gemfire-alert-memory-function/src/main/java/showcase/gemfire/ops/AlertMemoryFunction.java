package showcase.gemfire.ops;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.function.Supplier;


/**
 *
 * Return true if memory heapo above threshold
 * @author gregory green
 */
public class AlertMemoryFunction implements Function, Declarable {

    private Logger logger  = LogManager.getLogger(AlertMemoryFunction.class);
    private final Supplier<Cache> cacheSupplier;
    private final static double DEFAULT_THRESHOLD = 95;

    /**
     * Default Constructor used when deploying function with
     * Gfsh
     */
    public AlertMemoryFunction()
    {
        this(CacheFactory::getAnyInstance);
    }

    /**
     * Constructor for the balancer
     * @param cacheSupplier the supplier for get cache (convenient for QQ verification)
     */
    public AlertMemoryFunction(Supplier<Cache> cacheSupplier) {
        this.cacheSupplier = cacheSupplier;
    }

    /**
     * Rebalance data if needed
     * @param functionContext the function context
     */
    @Override
    public void execute(FunctionContext functionContext) {
        ResultSender<Boolean> sender = functionContext.getResultSender();

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

        long used = heapUsage.getUsed();
        long max = heapUsage.getMax();
        double usage = (double) used / max;

        logger.info("Heap usage: {}} ({}}MB used of {}}MB)", usage * 100, used / (1024 * 1024), max / (1024 * 1024));
        var memoryUsageThreshold = getThreshold(functionContext.getArguments());

        if (usage > memoryUsageThreshold) {
            logger.warn("Heap memory usage detected higher than threshold {}",memoryUsageThreshold);
            sender.lastResult(true);
        }
        else
            sender.lastResult(false);
    }

    private double getThreshold(Object arguments) {
        if(arguments instanceof String[] args){
            return Double.parseDouble(args[0]);
        }
        return DEFAULT_THRESHOLD;
    }

    @Override
    public String getId() {
        return "AlertMemoryFunction";
    }
}

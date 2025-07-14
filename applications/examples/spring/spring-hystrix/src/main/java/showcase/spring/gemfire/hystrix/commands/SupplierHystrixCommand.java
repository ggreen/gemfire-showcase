package showcase.spring.gemfire.hystrix.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import java.util.function.Supplier;

/**
 * Calls the supplier get operation wrapped with Hystrix circuit breaker
 * @param <T> the region operation value type
 * @author gregory green
 */
public class SupplierHystrixCommand<T> extends HystrixCommand<T> {
    private final Supplier<T> supplier;
    private final Supplier<T> fallbackSupplier;


    /**
     * Creates a new instance of the SupplierHystrixCommand
     * @param supplier the normal supplier
     * @param fallbackSupplier the supplier if the circuit breaker is open
     * @param timeoutMilliseconds the hystrix timeout in milliseconds
     * @param coreSize the threading considerations for the host client cores/vcpu counts
     */
    public SupplierHystrixCommand(Supplier<T> supplier,
                                  Supplier<T> fallbackSupplier,
                                  int timeoutMilliseconds, int coreSize) {
        super(Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("GemFireCommands"))
                .andCommandPropertiesDefaults(
                        com.netflix.hystrix.HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                                .withExecutionTimeoutInMilliseconds(timeoutMilliseconds)
                                .withCircuitBreakerEnabled(true)
                )
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(coreSize)
        ));

        this.supplier = supplier;
        this.fallbackSupplier = fallbackSupplier;
    }

    /**
     * This method is called by Hystrix during execution
     * @return the supplier get
     * @throws Exception
     */
    @Override
    protected T run() throws Exception {
        return supplier.get();
    }

    /**
     * This method is called once the circuit is open
     * @return the fallbackSupplier.get
     */
    @Override
    protected T getFallback() {
        return fallbackSupplier.get();
    }
}

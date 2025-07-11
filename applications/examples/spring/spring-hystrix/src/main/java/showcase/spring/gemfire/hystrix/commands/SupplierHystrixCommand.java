package showcase.spring.gemfire.hystrix.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import java.util.function.Supplier;

public class SupplierHystrixCommand<T> extends HystrixCommand<T> {
    private final Supplier<T> function;
    private final Supplier<T> fallback;

    public SupplierHystrixCommand(Supplier<T> function,
                                  Supplier<T> fallback,
                                  int timeoutMilliseconds) {
        super(Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("GemFireCommands"))
                .andCommandPropertiesDefaults(
                        com.netflix.hystrix.HystrixCommandProperties.Setter()
                                .withExecutionTimeoutInMilliseconds(timeoutMilliseconds)
                                .withCircuitBreakerEnabled(true)
                ));
        this.function = function;
        this.fallback  = fallback;

    }

    @Override
    protected T run() throws Exception {
        return function.get();
    }

    @Override
    protected T getFallback() {
        return fallback.get();
    }
}

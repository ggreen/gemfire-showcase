package showcase.gemfire.demo.functions.locking.constants;

public interface LockingErrors {

    String FILTER_REQUIRED = """
            A filter with the lock key is required.
            See FunctionService.onRegion(region)
            .withFilter(keysToFilter)
            .withArgs(someArguments);
            OR gfsh> execute function --id=AcquireSemaphoreFunction  --filter=junit --region=test --arguments=1,999,MINUTES
            """;
    String ARGUMENTS_REQUIRED = """
            Function input arguments required in form of the string arrays:
            - [0] The number of permits is passed in as an argument (see java.util.concurrent.Semaphore docs)
            - [1] The timeOut value
            - [2] The timeout unit (NANOSECONDS,MICROSECONDS,MILLISECONDS,SECONDS,MINUTES,HOURS,DAYS) 
            """;
}

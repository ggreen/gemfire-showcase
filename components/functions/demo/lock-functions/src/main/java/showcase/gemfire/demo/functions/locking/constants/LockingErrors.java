package showcase.gemfire.demo.functions.locking.constants;

public interface LockingErrors {

    String FILTER_REQUIRED = """
            A filter with the lock key is required.
            See FunctionService.onRegion(region)
            .withFilter(keysToFilter)
            .withArgs(someArguments);
            OR gfsh> execute function --id=AcquireSemaphoreFunction  --filter=junit --region=test --arguments=1,999,MINUTES
            """;
}

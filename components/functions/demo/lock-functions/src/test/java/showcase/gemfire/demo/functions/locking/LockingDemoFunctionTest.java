package showcase.gemfire.demo.functions.locking;

import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import showcase.gemfire.demo.functions.locking.demo.LockingDemoFunction;

import java.util.Set;

class LockingDemoFunctionTest {

    private int port = 10334;
    String host = "127.0.0.1";
    private final String regionName = "test";
    private final String filterKey = "junit";
    private final String seconds = String.valueOf(30*1000);
    private final String[] arguments = {"lockService",filterKey, seconds};
    private final String functionId = new LockingDemoFunction().getId();
    private final ResultCollector collector = new JunitResultConnection();

    @Test
    @EnabledIfSystemProperty(
            named = "integration.testing",
            matches = "true"
    )
    void execute() {
        
        try(var cache = new ClientCacheFactory().addPoolLocator(host,port)
                .create())
        {
            var region = cache.createClientRegionFactory(ClientRegionShortcut.PROXY)
                    .create(regionName);
            
            var results = FunctionService.onRegion(region).withFilter(Set.of(filterKey))
                    .setArguments(arguments)
                    .withCollector(collector)
                    .execute(functionId);

            System.out.println("results="+results.getResult());
        }
        
        
    }
}
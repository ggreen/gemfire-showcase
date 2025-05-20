package showcase.gemfire.balancer;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.control.RebalanceFactory;
import org.apache.geode.cache.control.RebalanceOperation;
import org.apache.geode.cache.control.RebalanceResults;
import org.apache.geode.cache.control.ResourceManager;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.server.CacheServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalancerFunctionTest {
    private BalancerFunction subject;
    @Mock
    private FunctionContext context;

    @Mock
    private ResultSender sender;
    @Mock
    private Cache cache;

    @Mock
    private ResourceManager resourceManager;

    @Mock
    private RebalanceFactory rebalanceFactory;
    @Mock
    private RebalanceOperation simulateOps;

    @Mock
    private RebalanceResults simulateOpsResults;
    @Mock
    private RebalanceOperation ops;
    @Mock
    private RebalanceResults opsResults;
    @Mock
    private CacheServer cacheServer;

    @BeforeEach
    void setUp() {
        subject = new BalancerFunction( () -> cache);
    }

    @Test
    void execute_simulatedTotalMembersExecutedOn_iszero() throws InterruptedException {
        when(cache.getResourceManager()).thenReturn(resourceManager);
        when(resourceManager.createRebalanceFactory()).thenReturn(rebalanceFactory);
        when(rebalanceFactory.simulate()).thenReturn(simulateOps);
        when((simulateOps.getResults())).thenReturn(simulateOpsResults);
        when(context.getResultSender()).thenReturn(sender);
        when(simulateOpsResults.getTotalMembersExecutedOn()).thenReturn(3);
        when(rebalanceFactory.start()).thenReturn(ops);
        when(ops.getResults()).thenReturn(opsResults);

        // Call execute
        subject.execute(context);

        // Verify a result or exception was sent
        verify(sender, times(1)).lastResult(any());
    }

    @Test
    void checkMinimumNumberOfMembers() {
        when(cache.getResourceManager()).thenReturn(resourceManager);
        when(context.getResultSender()).thenReturn(sender);

        String[] min3Servers = {"3"};
        List<CacheServer> oneCacheServer = asList(cacheServer);
        when(context.getArguments()).thenReturn(min3Servers);
        when(cache.getCacheServers()).thenReturn(oneCacheServer);


        // Call execute
        subject.execute(context);

        // Verify results
        verify(rebalanceFactory,never()).simulate();
        verify(sender, times(1)).lastResult(any());
    }
}
package showcase.gemfire.ops;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.server.CacheServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertMemoryFunctionTest {
    private AlertMemoryFunction subject;
    @Mock
    private FunctionContext context;

    @Mock
    private ResultSender sender;
    @Mock
    private Cache cache;

     @Mock
    private CacheServer cacheServer;

    @BeforeEach
    void setUp() {
        subject = new AlertMemoryFunction( () -> cache);
    }

    @Test
    void execute_noServersArguments() throws InterruptedException {
        when(context.getResultSender()).thenReturn(sender);

        // Call execute
        subject.execute(context);

        // Verify a result or exception was sent
        verify(sender, times(1)).lastResult(any());
    }

}
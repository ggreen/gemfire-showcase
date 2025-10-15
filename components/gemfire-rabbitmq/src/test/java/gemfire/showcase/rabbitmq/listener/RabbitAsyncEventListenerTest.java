package gemfire.showcase.rabbitmq.listener;

import gemfire.showcase.rabbitmq.RabbitPublisher;
import nyla.solutions.core.util.settings.Settings;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.pdx.PdxInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class RabbitAsyncEventListenerTest {

    @Mock
    private RabbitPublisher publisher;

    @Mock
    private RabbitAsyncEventListener subject;

    @Mock
    private AsyncEvent asyncEvent;

    @Mock
    private Function<Object,byte[]> converter;
    @Mock
    private Cache cache;
    @Mock
    private Properties properties;
    @Mock
    private Settings settings;

    @BeforeEach
    void setUp() {
        subject = new RabbitAsyncEventListener(settings, publisher,converter);
    }

    @Test
    void given_event_when_processEvent_then_dataPublished() throws IOException, InterruptedException, TimeoutException {

        List<AsyncEvent> events = asList(asyncEvent);
        boolean actual = subject.processEvents(events);

        assertTrue(actual);

        verify(publisher).publish(any(),any());
    }

    @Test
    @EnabledIfSystemProperty(named = "integration.test", matches = "true")
    void publishWithRabbitM() throws URISyntaxException, NoSuchAlgorithmException, IOException, KeyManagementException, TimeoutException, InterruptedException {

        RabbitAsyncEventListener  subject = new RabbitAsyncEventListener();
        subject.send("JUNIT TEST".getBytes(StandardCharsets.UTF_8),"junit.test");

    }

    @Test
    void setProperties_from_initialize_with_cache() {
        subject.initialize(cache,properties);

        verify(settings).setProperties(any());

    }
}
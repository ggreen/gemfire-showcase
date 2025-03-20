package showcase.gemfire.micrometer.metrics;

import org.apache.geode.metrics.MetricsSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PerformanceMetricsPublishingServiceTest {


    private PerformanceMetricsPublishingService subject;

    @Mock
    private MetricsSession metricsSession;

    @BeforeEach
    void setUp() {
        subject = new PerformanceMetricsPublishingService();
    }

    @Test
    void start() {
        subject.start(metricsSession);

        verify(metricsSession).addSubregistry(any());
    }
}
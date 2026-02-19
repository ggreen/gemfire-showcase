package io.cloudNativeData.gemfire.latency.listeners;

import org.apache.geode.cache.wan.GatewayQueueEvent;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.WritablePdxInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.apache.geode.internal.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StartTimeLatencyGatewayEventFilterTest {

    private StartTimeLatencyGatewayEventFilter subject;

    @Mock
    private PdxInstance latencyMetrics;

    @Mock
    private GatewayQueueEvent<String,PdxInstance> event;

    @Mock
    private WritablePdxInstance writer;


    @BeforeEach
    void setUp() {
        subject = new StartTimeLatencyGatewayEventFilter();
    }

    @Test
    void given_gw_event_when_enqueue_then_save_key_time() {

        when(event.getDeserializedValue()).thenReturn(latencyMetrics);
        when(latencyMetrics.createWriter()).thenReturn(writer);

        assertTrue(subject.beforeEnqueue(event));

        verify(latencyMetrics).createWriter();
    }

    @Test
    void beforeTransmit() {
        assertTrue(subject.beforeTransmit(event));
    }
}
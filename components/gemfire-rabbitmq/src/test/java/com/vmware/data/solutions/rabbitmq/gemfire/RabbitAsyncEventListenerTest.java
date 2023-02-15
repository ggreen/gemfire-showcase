package com.vmware.data.solutions.rabbitmq.gemfire;

import com.vmware.data.solutions.rabbitmq.RabbitPublisher;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.pdx.PdxInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
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
    private Function<PdxInstance,byte[]> converter;

    @BeforeEach
    void setUp() {
        subject = new RabbitAsyncEventListener(publisher,converter);
    }

    @Test
    void given_event_when_processEvent_then_dataPublished() throws IOException, InterruptedException, TimeoutException {

        List<AsyncEvent> events = asList(asyncEvent);
        boolean actual = subject.processEvents(events);

        assertTrue(actual);

        verify(publisher).publish(any(),any());
    }
}
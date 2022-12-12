package com.vmware.data.solutions.rabbitmq.gemfire;

import com.vmware.data.solutions.rabbitmq.RabbitPublisher;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class RabbitAsyncEventListenerTest {

    @Mock
    private RabbitPublisher publisher;

    @Mock
    private RabbitAsyncEventListener subject;

    @Mock
    private AsyncEvent e1;

    @Test
    void given_event_when_processEvent_then_dataPublished() {

        subject = new RabbitAsyncEventListener(publisher);


        List<AsyncEvent> events = asList(e1);
        subject.processEvents(events);
    }
}
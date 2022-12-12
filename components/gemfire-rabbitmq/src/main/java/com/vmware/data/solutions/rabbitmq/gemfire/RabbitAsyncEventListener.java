package com.vmware.data.solutions.rabbitmq.gemfire;

import com.vmware.data.solutions.rabbitmq.RabbitPublisher;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;

import java.util.List;

public class RabbitAsyncEventListener implements AsyncEventListener {

    private final RabbitPublisher publisher;

    public RabbitAsyncEventListener(RabbitPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public boolean processEvents(List<AsyncEvent> events) {
        return false;
    }
}

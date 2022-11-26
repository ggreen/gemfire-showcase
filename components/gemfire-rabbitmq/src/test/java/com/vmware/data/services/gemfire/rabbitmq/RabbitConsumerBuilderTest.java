package com.vmware.data.services.gemfire.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RabbitConsumerBuilderTest {

    private String queueName = "test";
    private String bindingRules = "#";

    private RabbitConsumerBuilder subject;
    private Channel consumerChannel;
    private Consumer consumer;
    private Connection connection;

    @BeforeEach
    void setUp() {

        consumerChannel = mock(Channel.class);
        consumer = mock(Consumer.class);
        connection = mock(Connection.class);

        subject = new RabbitConsumerBuilder(connection);

    }

    @Test
    void given_queueName_when_queue_then_consumer_queueAdded() throws IOException {

        subject.queue(queueName, bindingRules);

        subject.build();

        verify(consumerChannel).basicConsume(anyString(),any(Consumer.class));
    }

    @Test
    void given_thread_when_getChannel_then_returnPerThread() throws IOException {

        when(connection.createChannel()).thenReturn(consumerChannel);

        assertEquals(consumerChannel, subject.getChannel());
    }
}
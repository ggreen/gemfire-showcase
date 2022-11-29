package com.vmware.data.solutions.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RabbitConsumerBuilderTest {

    private String queueName = "test";
    private String bindingRules = "#";

    private RabbitConsumerBuilder subject;
    private Channel consumerChannel;
    private Consumer consumer;
    private RabbitConnectionCreator connectionCreator;
    private Connection connection;

    @BeforeEach
    void setUp() {

        consumerChannel = mock(Channel.class);
        connection = mock(Connection.class);
        consumer = mock(Consumer.class);
        connectionCreator = mock(RabbitConnectionCreator.class);
        when(connectionCreator.getConnection()).thenReturn(connection);
        when(connectionCreator.getChannel()).thenReturn(consumerChannel);

        subject = new RabbitConsumerBuilder(connectionCreator);

    }

    @Test
    void given_queueName_when_queue_then_consumer_queueAdded() throws IOException {

        subject.queue(queueName, bindingRules).addConsumer(consumer);

        subject.build();

        verify(consumerChannel).basicConsume(anyString(),any(Consumer.class));
    }
}
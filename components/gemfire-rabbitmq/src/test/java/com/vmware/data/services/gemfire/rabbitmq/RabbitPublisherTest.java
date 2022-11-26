package com.vmware.data.services.gemfire.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test for RabbitPublisher
 * @author gregory green
 */
class RabbitPublisherTest {

    private Channel channel;
    private String routingKey = "routing";
    private String exchange = "amqp.topic";

    @BeforeEach
    void setUp() {

        channel = mock(Channel.class);
    }

    @Test
    void given_rabbit_when_send_then_sendMessage() throws IOException {

        RabbitPublisher subject = new RabbitPublisher(channel, exchange);

        byte[] message = "Hello world".getBytes(StandardCharsets.UTF_8);

        subject.send(message,routingKey);

        verify(channel).basicPublish(anyString(),anyString(),any(Boolean.class),any(Boolean.class),nullable(AMQP.BasicProperties.class),any());

    }
}
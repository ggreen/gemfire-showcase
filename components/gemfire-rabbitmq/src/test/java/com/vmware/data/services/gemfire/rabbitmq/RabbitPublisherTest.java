package com.vmware.data.services.gemfire.rabbitmq;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;

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


}
package com.vmware.data.services.gemfire.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
 * @author gregory green
 */
public class RabbitPublisher {
    private final Channel channel;
    private final String exchange;

    private boolean mandatory = true;
    private  boolean immediate = true;
    private AMQP.BasicProperties properties;

    public RabbitPublisher(Channel channel, String exchange) {
        this.channel = channel;
        this.exchange = exchange;
    }

    public void send(byte[] body, String routingKey) throws IOException {
        channel.basicPublish(exchange,routingKey,mandatory,immediate,properties,body);
    }
}

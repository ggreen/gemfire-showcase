package com.vmware.data.solutions.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.vmware.data.solutions.rabbitmq.gemfire.Env;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author gregory green
 */
public class RabbitPublisher implements AutoCloseable{

    private final AMQP.BasicProperties basicProperties;
    private final RabbitConnectionCreator creator;
    private final String exchange;
    private final boolean requireReliableDelivery;

    private long waitFromConfirmationTimeSpan;

    private int WAIT_FOR_CONFIRMATION_SECONDS = Env.getPropertyInteger("RABBIT_WAIT_FOR_CONFIRMATION_SECS",30);


    public RabbitPublisher(RabbitConnectionCreator creator, String exchange, AMQP.BasicProperties basicProperties, boolean confirmPublish)
    {
        this.creator = creator;
        this.exchange = exchange;
        this.basicProperties = basicProperties;
        this.requireReliableDelivery = confirmPublish;

        if(confirmPublish)
            waitFromConfirmationTimeSpan  = WAIT_FOR_CONFIRMATION_SECONDS * 1000;
    }

    public void close() throws Exception {
        this.creator.close();
    }

    public void publish(byte[] body, String routingKey) throws IOException, InterruptedException, TimeoutException {
        if (body == null || body.length == 0)
            throw new IllegalArgumentException("Body cannot be null or empty");

        if (routingKey == null)
            throw new IllegalArgumentException("routingKey cannot be null");

        creator.getChannel().basicPublish( exchange,
            routingKey,
            true,
             basicProperties,
            body);

        if (this.requireReliableDelivery)
        {
            creator.getChannel().waitForConfirmsOrDie(waitFromConfirmationTimeSpan);
        }

    }
}

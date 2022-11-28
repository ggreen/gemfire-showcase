package com.vmware.data.services.gemfire.rabbitmq;

import com.rabbitmq.client.Consumer;

import java.io.IOException;
import java.util.List;

/**
 * Builder for RabbitMQ consumers
 * @author gregory Green
 */
public class RabbitConsumerBuilder extends AbstractRabbitBuilder {

    private Consumer consumer;

    public RabbitConsumerBuilder(RabbitConnectionCreator connection) {
        super(connection);
    }

    public RabbitConsumerBuilder queue(String queueName, String bindingRules) {
        this.addQueue(queueName,bindingRules);

        return this;
    }

    public RabbitConsumer build() {
        try {
            super.constructQueues();

            List<String> queueNames = super.getQueueNames();

            for(String queueName : queueNames)
            {
                this.getChannel().basicConsume(queueName,consumer);
            }

            return new RabbitConsumer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public RabbitConsumerBuilder addConsumer(Consumer consumer) {
        this.consumer = consumer;

        return this;
    }
}


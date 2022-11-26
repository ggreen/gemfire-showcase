package com.vmware.data.services.gemfire.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import nyla.solutions.core.patterns.creational.Creator;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Rabbit {


    private final Creator<Connection> connectionCreator;

    Rabbit(Creator<Connection> connectionCreator) {
        this.connectionCreator = connectionCreator;

    }


    public static RabbitBuilder builder() {
        return new RabbitBuilder();
    }



    public void registerConsumer(String queueName, String consumerTag, Consumer consumer) {

    }

    public RabbitPublisherBuilder publisherBuilder() {
        return null;
    }

    public RabbitConsumerBuilder consumerBuilder() {
        return null;
    }
}

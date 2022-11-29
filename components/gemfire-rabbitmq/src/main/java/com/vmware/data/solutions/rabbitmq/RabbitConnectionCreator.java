package com.vmware.data.solutions.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public interface RabbitConnectionCreator extends AutoCloseable {
    Connection getConnection();

    Channel getChannel();
}

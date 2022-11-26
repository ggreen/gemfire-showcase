package com.vmware.data.services.gemfire.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import nyla.solutions.core.patterns.creational.Creator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class RabbitBuilder implements Creator<Connection> {
    private String uri;


    RabbitBuilder()
    {}

    public RabbitBuilder connectUri(String uri)
    {
        this.uri = uri;
        return this;
    }

    public Rabbit connect() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        return new Rabbit(this);
    }

    @Override
    public Connection create() {
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setUri(uri);

            return factory.newConnection();
        } catch (NoSuchAlgorithmException|URISyntaxException|KeyManagementException|IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}

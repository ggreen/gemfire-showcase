package com.vmware.data.services.gemfire.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import nyla.solutions.core.patterns.creational.Creator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * @author gregory green
 */
class RabbitTest {

    private Rabbit subject;

    private Channel publisherChannel;
    private Channel consumerChannel;
    private Creator<Connection> connectionCreator;
    private Connection connection;

    @BeforeEach
    void setUp() {
        connection = mock(Connection.class);

        this.connectionCreator = mock(Creator.class);

        publisherChannel = mock(Channel.class);

        subject = new Rabbit(connectionCreator);


    }




    @Test
    void connect() throws URISyntaxException, NoSuchAlgorithmException, IOException, KeyManagementException, TimeoutException {
        String url = "amqp://localhost:5672";
        Rabbit subject = Rabbit.builder().connectUri(url).connect();

        assertNotNull(subject);
    }

    @Test
    void when_publisher_then_notNull() throws IOException {

        when(connection.createChannel()).thenReturn(publisherChannel);

        byte[] message = "Hello World".getBytes(StandardCharsets.UTF_8);

        String exchange = "amqp.topic";
        assertNotNull(subject.publisherBuilder().exchange(exchange).build());
    }


}
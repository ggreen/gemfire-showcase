package com.vmware.data.services.gemfire.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import nyla.solutions.core.patterns.creational.Creator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.mock;


/**
 * @author gregory green
 */
class RabbitTest {

    private Rabbit subject;

    private Channel publisherChannel;
    private Channel consumerChannel;
    private Creator<Connection> connectionCreator;
    private Connection connection;

    @BeforeAll
    static void beforeAll() {
        System.setProperty("RABBIT_CLIENT_NAME","junit-test");
        System.setProperty("RABBIT_URIS","localhost:5672");
    }

    @BeforeEach
    void setUp() {
        connection = mock(Connection.class);

        this.connectionCreator = mock(Creator.class);

        publisherChannel = mock(Channel.class);

    }




    @Test
    void connect() throws URISyntaxException, NoSuchAlgorithmException, IOException, KeyManagementException, TimeoutException, InterruptedException {


        String url = "amqp://localhost:5672";


        Rabbit subject = Rabbit.connect();
        subject.PublishBuilder().exchange("hello").AddQueue("world","#")
                .build().publish("Imani".getBytes(StandardCharsets.UTF_8),"Green");

    }


}
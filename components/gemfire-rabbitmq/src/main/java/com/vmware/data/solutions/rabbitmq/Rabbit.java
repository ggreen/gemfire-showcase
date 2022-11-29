package com.vmware.data.solutions.rabbitmq;

import com.rabbitmq.client.*;
import nyla.solutions.core.util.Text;
import nyla.solutions.core.util.settings.ConfigSettings;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Rabbit implements RabbitConnectionCreator, BlockedListener, ShutdownListener, AutoCloseable {
    private final ConnectionFactory factory;
    private Connection connection = null;
    private final List<URI> endpoints;

    private static final int DEFAULT_CONNECTION_RETRY_SECS = 15;

    private Channel channel;

    private short qosPreFetchLimit;


    private Rabbit(List<URI> endpoints, Boolean sslEnabled, String clientProvidedName, int networkRecoveryIntervalSecs, Short qosPreFetchLimit)
    {
        factory = new ConnectionFactory();
        this.endpoints = endpoints;
    }
    private Rabbit(ConnectionFactory factory, List<URI> endpoints, Boolean sslEnabled, Short qosPreFetchLimit) {
        this.factory = factory;

        this.qosPreFetchLimit = qosPreFetchLimit;
        this.endpoints = endpoints;
    }

    private static List<Address> toAddresses(List<URI> endpoints) {
        if (endpoints == null)
            return null;

        List<Address> amqpEndpoints = new ArrayList<>(endpoints.size());
        for(URI uri : endpoints)
        {
            amqpEndpoints.add(Address.parseAddress(uri.toString()));
        }
        return amqpEndpoints;
    }


    public Connection getConnection()  {

        try
        {
            if (this.connection == null) {
                this.connection = newConnection();
                return this.connection;
            }

            if (!this.connection.isOpen()) {

                try {
                    System.out.println("WARNING: DISPOSING connection");
                    this.connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                this.resetConnection();

            }
            return this.connection;
        }
         catch (IOException | InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private void resetConnection() throws InterruptedException {

        while (this.connection == null || !this.connection.isOpen()) {
            try {
                this.connection = newConnection();
                this.channel = connection.createChannel();
                System.out.println("INFO: connected to cluster");

            } catch (Exception e) {
                System.out.printf("WARNING: %s restarting in %x seconds%n", e.getMessage(), DEFAULT_CONNECTION_RETRY_SECS);
                Thread.sleep(DEFAULT_CONNECTION_RETRY_SECS * 1000);
            }
        }

    }

    private Connection newConnection() throws IOException, TimeoutException {
        factory.setHandshakeTimeout(DEFAULT_CONNECTION_RETRY_SECS * 1000);
        factory.setNetworkRecoveryInterval(DEFAULT_CONNECTION_RETRY_SECS);

        Connection rabbitConnection = null;
        if (this.endpoints != null && this.endpoints.size() > 0) {
            rabbitConnection = factory.newConnection(
                    toAddresses(endpoints));
        } else {
            rabbitConnection = factory.newConnection();
        }

        rabbitConnection.addBlockedListener(this);
        rabbitConnection.addShutdownListener(this);

        return rabbitConnection;
    }


    public RabbitPublisherBuilder publishBuilder() {
        return new RabbitPublisherBuilder(this, qosPreFetchLimit);
    }

    public static Rabbit connect() throws MalformedURLException, URISyntaxException {
        ConfigSettings config = new ConfigSettings();
        int networkRecoveryIntervalSecs = config.getPropertyInteger("RABBIT_CONNECTION_RETRY_SECS", DEFAULT_CONNECTION_RETRY_SECS);
        String clientName = config.getProperty("RABBIT_CLIENT_NAME");
        Short qosPreFetchLimit = Short.parseShort(config.getProperty("RABBIT_PREFETCH_LIMIT", "1000"));

        String urisText = config.getProperty("RABBIT_URIS");
            boolean sslEnabled = urisText.toLowerCase().contains("amqps:");
            return new Rabbit(parseUrisToEndPoints(urisText), sslEnabled, clientName, networkRecoveryIntervalSecs, qosPreFetchLimit);
    }

    private static List<URI> parseUrisToEndPoints(String urisText)  throws URISyntaxException {
        if (Text.isNull(urisText)) {
            throw new IllegalArgumentException("URIS required");
        }

        String[] urisArray = urisText.split(",");
        List<URI> list = new ArrayList<URI>();
        for (String uri : urisArray) {
            list.add(new URI(uri));
        }

        return list;

    }

    public void close() throws IOException, TimeoutException {
        System.out.println("WARNING: %%%%%% dispose connection");
        if (this.channel != null)
            this.channel.close();

        if (this.connection != null)
            this.connection.close();
    }

    public Channel getChannel()  {

        try{
            if (this.channel == null) {
                this.channel = getConnection().createChannel();
                return this.channel;
            }

            if (this.channel.isOpen() && this.connection.isOpen()) {
                if (this.channel.getNextPublishSeqNo() == 0) {
                    if (this.channel != null) {
                        System.out.println("WARNING:NextPublishSeqNo == 0");
                        this.channel.close();
                        this.channel = null;
                    }


                    if (this.connection != null) {
                        this.connection.close();
                        this.connection = null;
                    }

                    this.resetConnection();
                    this.channel.confirmSelect();
                }
                return this.channel;
            } else {
                try {
                    System.out.println("WARNING: DISPOSING connection");

                    this.channel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                this.channel = getConnection().createChannel();
                return this.channel;
            }
        } catch (IOException | InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleBlocked(String reason)  {

    }

    @Override
    public void handleUnblocked()  {

    }

    @Override
    public void shutdownCompleted(ShutdownSignalException cause) {
        System.out.println("shutdownCompleted:" + cause);
    }
}

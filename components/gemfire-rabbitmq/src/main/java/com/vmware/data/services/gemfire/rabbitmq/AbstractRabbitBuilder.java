package com.vmware.data.services.gemfire.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public  abstract class AbstractRabbitBuilder {
    private final Connection connection;

    private final Map<Long, Channel> channelMap;

    private boolean durableQueue = true;
    private boolean exclusiveQueue = false;
    private boolean autoDeleteQueue = false;
    private Map<String, Object> queueArgument;
    private String exchangeName;
    private String uri;

    protected Connection getConnection() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        return factory.newConnection();
    }

    protected AbstractRabbitBuilder(Connection connection) {
        this.connection = connection;
        this.channelMap = new ConcurrentHashMap<>();
    }

    protected void declareQueue(String queueName, String routingKey) throws IOException {
        Channel channel = getChannel();

        //String queue, boolean durable, boolean exclusive, boolean autoDelete,
        //                                 Map<String, Object> argument
        channel.queueDeclare(queueName,durableQueue,exclusiveQueue,autoDeleteQueue,queueArgument);

       channel.queueBind(queueName,exchangeName,routingKey);
    }


    public Channel getChannel() throws IOException {

        long threadId = Thread.currentThread().getId();

        Channel channel = channelMap.get(threadId);

        if(channel  == null){
            channel = connection.createChannel();

            channelMap.put(threadId,channel);
        }

        return channel;

    }
}

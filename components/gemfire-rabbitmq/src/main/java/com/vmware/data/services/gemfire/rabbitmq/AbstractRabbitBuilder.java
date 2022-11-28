package com.vmware.data.services.gemfire.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public  abstract class AbstractRabbitBuilder {
    private final RabbitConnectionCreator connectionCreator;

    private final Map<Long, Channel> channelMap;

    private boolean durableQueue = true;
    private boolean exclusiveQueue = false;
    private boolean autoDeleteQueue = false;
    private Map<String, Object> queueArgument;
    private String exchangeName;
    private String uri;
    private Set<String[]> queueRules = new HashSet<>();

    protected RabbitConnectionCreator getConnectionCreator() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException {

        return this.connectionCreator;
    }

    protected AbstractRabbitBuilder(RabbitConnectionCreator connection) {
        this.connectionCreator = connection;
        this.channelMap = new ConcurrentHashMap<>();
    }

    protected void declareQueue(String queueName, String routingKey) throws IOException {
        Channel channel = getChannel();

        //String queue, boolean durable, boolean exclusive, boolean autoDelete,
        //                                 Map<String, Object> argument
        channel.queueDeclare(queueName,durableQueue,exclusiveQueue,autoDeleteQueue,queueArgument);

       channel.queueBind(queueName,exchangeName,routingKey);
    }

    protected void addQueue(String queueName, String bindingRules)
    {
        String[] queueRule = {queueName, bindingRules};

        queueRules.add(queueRule);
    }

    public Channel getChannel() throws IOException {

        long threadId = Thread.currentThread().getId();

        Channel channel = channelMap.get(threadId);

        if(channel  == null){
            channel = connectionCreator.getChannel();

            channelMap.put(threadId,channel);
        }

        return channel;

    }


    protected void constructQueues() throws IOException {

        for (String[] queueBindRule: this.queueRules) {
                declareQueue(queueBindRule[0],queueBindRule[1]);
            }
    }


    protected List<String> getQueueNames() {
        return this.queueRules.stream().map(queueRule -> queueRule[0])
                .collect(Collectors.toList());
    }
}

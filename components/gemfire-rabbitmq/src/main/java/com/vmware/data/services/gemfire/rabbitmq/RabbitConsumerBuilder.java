package com.vmware.data.services.gemfire.rabbitmq;

import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RabbitConsumerBuilder extends AbstractRabbitBuilder {
    private final List<String[]> queueRules;

    public RabbitConsumerBuilder(Connection connection) {
        super(connection);

        this.queueRules = new ArrayList<>();
    }

    public void queue(String queueName, String bindingRules) {
        String[] queueRule = {queueName, bindingRules};
        queueRules.add(queueRule);
    }

    public RabbitConsumer build() {
        try {
            Connection connection = getConnection();

            for (String[] queueBindRule: this.queueRules) {
                declareQueue(queueBindRule[0],queueBindRule[1]);
            }

            return new RabbitConsumer();
        } catch (URISyntaxException| NoSuchAlgorithmException | KeyManagementException | IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }



}


package com.vmware.data.solutions.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RabbitBuilderTest {

    private RabbitConnectionCreator connectionCreator;
    private short qosPreFetchLimit = 3;
    private RabbitBuilder subject;
    private String exchange = "exchange";
    private Channel channel;
    private Connection connection;
    private String queue = "queue";
    private String routingKey = "#";

    @BeforeEach
    void setUp() {
        connection = mock(Connection.class);
        channel = mock(Channel.class);
        connectionCreator = mock(RabbitConnectionCreator.class);

        when(connectionCreator.getConnection()).thenReturn(connection);
        when(connectionCreator.getChannel()).thenReturn(channel);


        subject = new RabbitBuilder(connectionCreator, qosPreFetchLimit);
    }

    @Test
    void getQueueArguments() {
        HashMap<String, Object> expected = new HashMap<>();
        expected.put("x-queue-type","quorum");

        subject.assignQueueType(RabbitQueueType.quorum);

        assertEquals(expected, subject.getQueueArguments());

    }

    @Test
    void given_assignQuorum_when_isQuorumQueues_then_isTrue() {

        subject.assignQueueTypeArgToQuorum();

        assertTrue(subject.isQuorumQueues());
    }

    @Test
    void given_queue_when_addQueueRoutingKey_then_assertQueueAdded() {

        String queue = "queue";
        String routingKey =  "#";
        subject.addQueueRoutingKey(queue,routingKey);

        List<String> expected = asList(queue);
        assertEquals(expected,subject.getQueueNames());
    }


    @Test
    void given_exchangeNotSet_when_constructExchange_then_throwsException() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> subject.constructExchange());
    }


    @Test
    void constructExchange() throws IOException {

        subject.setExchange(exchange);
        subject.constructExchange();

        verify(channel).exchangeDeclare(anyString(),anyString(),anyBoolean());
    }

    @Test
    void given_queuesAdded_when_constructQueues_then_queueDeclared() throws IOException {

        subject.addQueueRoutingKey(queue,routingKey);

        subject.constructQueues();

        verify(channel).queueDeclare(anyString(),anyBoolean(),anyBoolean(),anyBoolean(),any());
    }

    @Test
    void given_noQueues_when_checkQueues_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> subject.checkQueues());
    }

    @Test
    void given_queues_when_checkQueues_then_doeNotThrowException() {
        subject.addQueueRoutingKey(queue,routingKey);

        assertDoesNotThrow(() -> subject.checkQueues());

    }

    @Test
    void given_quorum_when_assignQueueTypeArgToQuorum_then_QueueTypeIsQuorum() {
        subject.assignQueueTypeArgToQuorum();

        assertEquals(subject.getQueueArguments().get("x-queue-type"),"quorum");
    }

    @Test
    void given_quorum_when_assignQueueType_then_QueueTypeIsQuorum() {
        subject.assignQueueType(RabbitQueueType.quorum);
        assertEquals(subject.getQueueArguments().get("x-queue-type"),"quorum");
    }

    @Test
    void assignQueueTypeArgClassic() {
        subject.assignQueueTypeArgClassic();

        assertEquals(subject.getQueueArguments().get("x-queue-type"),"classic");
    }

    @Test
    void given_inMemoryZero_when_assignQuorumQueueMaxInMemoryZero_then_queueueArgs_hasZeroValue() {

        subject.assignQuorumQueueMaxInMemoryZero();


        assertEquals(subject.getQueueArguments().get("x-max-in-memory-length"),"0");
    }

    @Test
    void given_classic_when_AssignQueueModeArgToLazy_queueModeIsLazy() {

        subject.assignQueueTypeArgClassic();

        subject.assignQueueModeArgToLazy();

        assertEquals(subject.getQueueArguments().get("x-queue-mode"),"lazy");
    }

    @Test
    void given_quorum_when_assignQueueModeArgToLazy_then_queueModeBlank() {

        subject.assignQueueTypeArgToQuorum();

        subject.assignQueueModeArgToLazy();

        assertEquals(subject.getQueueArguments().get("x-queue-mode"),"lazy");
    }

    @Test
    void getExchange() {
        subject.setExchange(exchange);
        assertEquals(exchange, subject.getExchange());
    }

    @Test
    void setExchange() {
    }
}
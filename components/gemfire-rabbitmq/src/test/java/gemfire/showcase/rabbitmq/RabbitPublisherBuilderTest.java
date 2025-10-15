package gemfire.showcase.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RabbitPublisherBuilderTest {

    private RabbitConnectionCreator connectionCreator;
    private Short qosPreFetchLimit = 10;
    private Channel channel;
    private Connection connection;
    private String exchange ="amqp.topic";

    @BeforeEach
    void setUp() {

        channel = mock(Channel.class);
        connectionCreator = mock(RabbitConnectionCreator.class);

        when(connectionCreator.getChannel()).thenReturn(channel);
        when(connectionCreator.getConnection()).thenReturn(connection);

    }

    @Test
    void given_exchangeNotSet_when_build_throwsException() throws IOException {

        RabbitPublisherBuilder subject = new RabbitPublisherBuilder(connectionCreator,qosPreFetchLimit);

        assertThrows(IllegalArgumentException.class, () -> subject.build());

    }

    @Test
    void given_exchange_when_build_exchange_declare() throws IOException {

        RabbitPublisherBuilder subject = new RabbitPublisherBuilder(connectionCreator,qosPreFetchLimit)
                .exchange(exchange);

        RabbitPublisher actual = subject.build();

        assertNotNull(actual);

        verify(channel).exchangeDeclare(anyString(),anyString(),anyBoolean());

    }
}
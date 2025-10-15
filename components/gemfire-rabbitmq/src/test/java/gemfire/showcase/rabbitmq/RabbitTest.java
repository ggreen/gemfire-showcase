package gemfire.showcase.rabbitmq;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;


/**
 * @author gregory green
 */
class RabbitTest {

    private Rabbit subject;

    private Channel publisherChannel;
    private Channel consumerChannel;
    private Supplier<Connection> connectionCreator;
    private Connection connection;

    @BeforeAll
    static void beforeAll() {
        System.setProperty("RABBIT_CLIENT_NAME","junit-test");
        System.setProperty("RABBIT_URIS","localhost:5672");
    }

    @BeforeEach
    void setUp() {
        connection = mock(Connection.class);

        this.connectionCreator = mock(Supplier.class);

        publisherChannel = mock(Channel.class);

    }


    @Test
    void toAddresses() throws URISyntaxException {
        //toAddresses(List<URI> endpoints)

        String host1 = "host1";
        String host2 = "host2";
        int port1 = 10001;
        int port2 = 10002;

        Address address1 = new Address(host1,port1);
        Address address2 = new Address(host2,port2);
        List<Address> expected = asList(address1,address2);

        String uriText1 = "amqp://user:pass@"+host1+":"+port1+"/vhost";
        String uriText2 = "amqp://user:pass@"+host2+":"+port2+"/vhost";

        List<URI> endpoints = asList(new URI(uriText1),new URI(uriText2));
        List<Address> actual = Rabbit.toAddresses(endpoints);
        assertNotNull(actual);

        assertEquals(expected, actual);

    }

    @Test
    @EnabledIfSystemProperty(named = "integration.test", matches = "true")
    void connect() throws URISyntaxException, NoSuchAlgorithmException, IOException, KeyManagementException, TimeoutException, InterruptedException {

        String url = "amqp://localhost:5672";

        Rabbit subject = Rabbit.connect();
        subject.publishBuilder().exchange("hello").AddQueue("world","#")
                .build().publish("Imani".getBytes(StandardCharsets.UTF_8),"Green");

    }


}
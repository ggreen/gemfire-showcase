package gemfire.showcase.rabbitmq;

import com.rabbitmq.client.AMQP;
import nyla.solutions.core.util.Config;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * @author gregory green
 */
public class RabbitPublisher implements AutoCloseable{

    private final AMQP.BasicProperties basicProperties;
    private final Supplier<RabbitConnectionCreator> creator;
    private final String exchange;
    private final boolean requireReliableDelivery;

    private long waitFromConfirmationTimeSpan;

    private final int WAIT_FOR_CONFIRMATION_SECONDS;


    public RabbitPublisher(Supplier<RabbitConnectionCreator> creator, String exchange, AMQP.BasicProperties basicProperties, boolean confirmPublish)
    {
        this.creator = creator;
        this.exchange = exchange;
        this.basicProperties = basicProperties;
        this.requireReliableDelivery = confirmPublish;

        WAIT_FOR_CONFIRMATION_SECONDS = Config.settings().getPropertyInteger("RABBIT_WAIT_FOR_CONFIRMATION_SECS",30);
        if(confirmPublish)
            waitFromConfirmationTimeSpan  = WAIT_FOR_CONFIRMATION_SECONDS * 1000;
    }

    public void close() throws Exception {
        this.creator.get().close();
    }

    public void publish(byte[] body, String routingKey) throws IOException, InterruptedException, TimeoutException {
        if (body == null || body.length == 0)
            throw new IllegalArgumentException("Body cannot be null or empty");

        if (routingKey == null)
            throw new IllegalArgumentException("routingKey cannot be null");

        creator.get().getChannel().basicPublish( exchange,
            routingKey,
            true,
             basicProperties,
            body);

        if (this.requireReliableDelivery)
        {
            creator.get().getChannel().waitForConfirmsOrDie(waitFromConfirmationTimeSpan);
        }

    }
}

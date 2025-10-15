package gemfire.showcase.rabbitmq.listener;

import com.rabbitmq.client.AMQP;
import gemfire.showcase.rabbitmq.Rabbit;
import gemfire.showcase.rabbitmq.RabbitPublisher;
import nyla.solutions.core.patterns.conversion.Converter;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.settings.Settings;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * An AsyncEventListener that publishes to a RabbitMQ exchange using the event key as the routing key
 * @author Gregory Green
 */
public class RabbitAsyncEventListener implements AsyncEventListener, Declarable {

    private final RabbitPublisher publisher;
    private final Function<Object,byte[]> converter;
    private final Logger log = LogManager.getLogger(RabbitAsyncEventListener.class);
    private final Settings settings;

    public RabbitAsyncEventListener() throws MalformedURLException, URISyntaxException {
        this(Config.settings());
    }

    public RabbitAsyncEventListener(Settings settings) throws MalformedURLException, URISyntaxException {
        this(settings,
                new RabbitPublisher(
                ()-> {
                    try {
                        return Rabbit.connect();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                },
                        settings.getProperty("RABBIT_EXCHANGE","amq.topic"),
                        new AMQP.BasicProperties(), true),
                new GemFireValueToByteConverter());
    }
    public RabbitAsyncEventListener(Settings settings, RabbitPublisher publisher,
                                    Function<Object,byte[]> converter) {
        this.converter= converter;
        this.publisher = publisher;
        this.settings = settings;
    }


    @Override
    public boolean processEvents(List<AsyncEvent> list) {

        for (AsyncEvent<String, Object> event: list) {
            try {
                String key = event.getKey();
                send(converter.apply(event.getDeserializedValue()),
                        key
                        );

                log.info("Published with routingKey: {} : ",key);
            } catch (IOException | InterruptedException | TimeoutException | RuntimeException e) {
                log.error(e);
                return false;
            }
        }
        return true;
    }

    /**
     * Sends the payload to the exchange with the given routing key
     * @param payload  the message body
     * @param routingKey the routing key
     * @throws IOException  when an IO error occurs
     * @throws InterruptedException when the thread is interrupted
     * @throws TimeoutException when a timeout occurs
     */
    protected void send(byte[] payload, String routingKey) throws IOException, InterruptedException, TimeoutException {
        publisher.publish(payload, routingKey);
    }

    @Override
    public void initialize(Cache cache, Properties properties) {
        log.info("Setting properties: {}",properties.keySet());
        settings.setProperties(properties);
    }
}

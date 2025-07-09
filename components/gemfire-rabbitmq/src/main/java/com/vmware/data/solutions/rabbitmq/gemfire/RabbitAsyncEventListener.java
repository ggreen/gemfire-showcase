package com.vmware.data.solutions.rabbitmq.gemfire;

import com.rabbitmq.client.AMQP;
import com.vmware.data.solutions.rabbitmq.Rabbit;
import com.vmware.data.solutions.rabbitmq.RabbitPublisher;
import nyla.solutions.core.util.Config;
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
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * @
 */
public class RabbitAsyncEventListener implements AsyncEventListener {

    private final RabbitPublisher publisher;
    private final Function<PdxInstance,byte[]> converter;
    private final Logger log = LogManager.getLogger(RabbitAsyncEventListener.class);

    public RabbitAsyncEventListener() throws MalformedURLException, URISyntaxException {
        this(new RabbitPublisher(
                Rabbit.connect(),
                        Config.getProperty("RABBIT_EXCHANGE"),
                        new AMQP.BasicProperties(), true),

                pdxInstance -> JSONFormatter.toJSONByteArray(pdxInstance));
    }
    public RabbitAsyncEventListener(RabbitPublisher publisher,Function<PdxInstance,byte[]> converter) {
        this.converter= converter;
        this.publisher = publisher;
    }


    @Override
    public boolean processEvents(List<AsyncEvent> list) {

        for (AsyncEvent<String, PdxInstance> event: list) {
            try {
                String key = event.getKey();
                //testing
                publisher.publish(converter.apply(event.getDeserializedValue()),
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
}

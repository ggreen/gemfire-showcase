package gemfire.showcase.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConfirmListener;

import java.io.IOException;

public class RabbitPublisherBuilder extends RabbitBuilder implements ConfirmListener {

    private RabbitConnectionCreator connectionCreator;
    private boolean persistent;
    private boolean confirmPublish;
    private String contentType;
    private boolean isConfirmPublish;
    private Short qosPreFetchLimit;


    public RabbitPublisherBuilder(RabbitConnectionCreator connectionCreator, Short qosPreFetchLimit)
    {
        super(connectionCreator, qosPreFetchLimit);

        this.connectionCreator = connectionCreator;
        this.persistent = true;
        connectionCreator.getChannel().addConfirmListener(this);
    }



    public RabbitPublisherBuilder exchange(String exchange)
    {
        super.setExchange(exchange);
        return this;
    }

    public RabbitPublisherBuilder AddQueue(String queue, String routingKey)
    {
        this.addQueueRoutingKey(queue, routingKey);
        return this;
    }

    public RabbitPublisherBuilder SetPersistent(boolean persistent)
    {
        this.persistent = persistent;
        return this;
    }
    public RabbitPublisher build() throws IOException {
        if (this.confirmPublish)
        {
            this.connectionCreator.getChannel().confirmSelect();
        }

        constructExchange();

        constructQueues();


        AMQP.BasicProperties.Builder propertiesBuilder = new AMQP.BasicProperties.Builder();

        propertiesBuilder.contentType(contentType);

        propertiesBuilder.deliveryMode(2); // persistent

        return new RabbitPublisher(() -> this.connectionCreator, getExchange(), propertiesBuilder.build(), isConfirmPublish());
    }

    public RabbitPublisherBuilder setExchangeType(RabbitExchangeType type)
    {
        this.setExchangeType(type);
        return this;
    }

    public RabbitPublisherBuilder setConfirmPublish()
    {
        this.isConfirmPublish = true;
        return this;
    }

    public RabbitPublisherBuilder SetQosPreFetchLimit(Short qos)
    {
        this.qosPreFetchLimit = qos;
        return this;
    }

    public RabbitPublisherBuilder setContentType(String contentType)
    {
        this.contentType = contentType;
        return this;
    }

    public RabbitPublisherBuilder setLazyQueue()
    {
        assignQueueModeArgToLazy();
        return this;
    }

    public RabbitPublisherBuilder UseQueueType(RabbitQueueType queueType)
    {
        this.assignQueueType(queueType);
        return this;
    }

    @Override
    public void handleAck(long deliveryTag, boolean multiple) throws IOException {

    }

    @Override
    public void handleNack(long deliveryTag, boolean multiple) throws IOException {

    }

    public boolean isConfirmPublish() {

        return confirmPublish;
    }
}

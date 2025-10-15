package gemfire.showcase.rabbitmq;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RabbitBuilder {
    public final static String QUORUM_QUEUE_TYPE = "quorum";
    public final static String CLASSIC_QUEUE_TYPE = "classic";
    private final static String QUEUE_MODE_PROP = "x-queue-mode";
    private final static String QUORUM_QUEUE_MAX_IN_MEMORY_LEN_PROP = "x-max-in-memory-length";
    private final static String QUEUE_TYPE_PROP = "x-queue-type";

    private RabbitConnectionCreator creator;


    private boolean lazyQueues;

    private boolean durable;
    private boolean autoDelete;

    private short qosPreFetchLimit;


    private boolean queueExclusive;
    private boolean quorumQueues;

    protected HashMap<String, Object> getQueueArguments() {
        return queueArguments;
    }

    private HashMap<String, Object> queueArguments;

    /// <summary>
    /// The client can request that messages be sent in advance so that when the client
    /// finishes processing a message, the following message is already held locally,
    /// rather than needing to be sent down the channel. Prefetching gives a performance improvement.
    /// This field specifies the prefetch window size in octets.
    /// The server will send a message in advance if it is equal to or smaller in size than the available prefetch
    /// size (and also falls into other prefetch limits).
    /// May be set to zero, meaning "no specific limit", although other prefetch limits may still apply.
    /// The prefetch-size is ignored if the no-ack option is set.
    /// The server MUST ignore this setting when the client is not processing any messages - i.e.
    /// the prefetch size does not limit the transfer of single messages to a client, only the sending in advance of more messages while the client still has one or more unacknowledged messages.
    /// </summary>
    private final int qosPrefetchSize = 0;
    private final boolean qosGlobal = false;
    protected Set<String[]> queues = new HashSet<String[]>();

    private RabbitExchangeType exchangeType = RabbitExchangeType.topic;

    private String exchange;

    protected RabbitBuilder(RabbitConnectionCreator connectionCreator, short qosPreFetchLimit)
    {
        init(connectionCreator,qosPreFetchLimit);

    }
    private void init(RabbitConnectionCreator connectionCreator, short qosPreFetchLimit)
    {
        this.creator = connectionCreator;

        durable = true;
        this.qosPreFetchLimit = qosPreFetchLimit;
        this.queueArguments = new HashMap<>();
    }




    protected boolean isQuorumQueues()
    {
            return this.queueArguments.containsKey(QUEUE_TYPE_PROP) &&
                    this.queueArguments.get(QUEUE_TYPE_PROP).equals(QUORUM_QUEUE_TYPE);
    }


    protected void addQueueRoutingKey(String queue, String routingKey)
    {
        if (queue == null || queue.isEmpty())
            throw new IllegalArgumentException("queue cannot be null or empty");

        if (routingKey == null)
            throw new IllegalArgumentException("routingKey cannot be null when adding a queue");

        String [] tuple = {queue, routingKey};
        this.queues.add(tuple);
    }


    protected void constructExchange() throws IOException {
        if (exchange == null || exchange.isEmpty())
            throw new IllegalArgumentException("Set Exchange required");

        creator.getChannel().basicQos(this.qosPrefetchSize, this.qosPreFetchLimit, this.qosGlobal);


        try{
            //String exchange, String type, boolean durable
            creator.getChannel().exchangeDeclare
                    (exchange, exchangeType.toString(), durable);

        }
        catch(IOException e)
        {
            System.out.println("WARNING: {e.Message} so using EXISTING exchange");
            creator.getChannel().exchangeDeclarePassive(exchange);
        }
    }
    protected void constructQueues() throws IOException {
        //CheckQueues();

        for (String[] queue : queues)
        {
            try
            {
                creator.getChannel().queueDeclare(queue[0], durable, queueExclusive, autoDelete, queueArguments);
            }
            catch(IOException e) {

                System.out.println("WARNING: {e.Message} so using EXISTING queue");

                creator.getChannel().queueDeclarePassive(queue[0]);
            }

            creator.getChannel().queueBind(queue[0], exchange, queue[1]);
        }
    }

    protected void checkQueues()
    {
        if (this.queues.isEmpty())
            throw new IllegalArgumentException("At Least 1 queue must be added");

    }

    protected List<String> getQueueNames() {
        return this.queues.stream().map(queueRule -> queueRule[0])
                .collect(Collectors.toList());
    }

    protected void assignQueueTypeArgToQuorum()
    {
        this.getQueueArguments().put(QUEUE_TYPE_PROP, QUORUM_QUEUE_TYPE);

        if (this.lazyQueues)
        {
            if (this.quorumQueues)
            {
                this.queueArguments.remove(QUEUE_MODE_PROP);
            }

            assignQuorumQueueMaxInMemoryZero();

        }
    }

    protected void assignQueueType(RabbitQueueType queueType)
    {
        switch(queueType)
        {
            case quorum:    this.assignQueueTypeArgToQuorum();
                break;
            case classic: this.assignQueueTypeArgClassic();
                break;
        }
    }


    protected void assignQueueTypeArgClassic()
    {
        this.queueArguments.put(QUEUE_TYPE_PROP, CLASSIC_QUEUE_TYPE);

    }

    protected void assignQuorumQueueMaxInMemoryZero()
    {
        this.queueArguments.put(QUORUM_QUEUE_MAX_IN_MEMORY_LEN_PROP, "0");
    }

    protected void assignQueueModeArgToLazy()
    {
        lazyQueues = true;

        if (this.quorumQueues)
        {
            assignQuorumQueueMaxInMemoryZero();
        }
        else
        {
            this.queueArguments.put(QUEUE_MODE_PROP,"lazy");
        }
    }

    public String getExchange() {
        return exchange;
    }

    protected void setExchange(String exchange) {
        this.exchange = exchange;
    }

    protected Channel getChannel() {
        return this.creator.getChannel();
    }
}

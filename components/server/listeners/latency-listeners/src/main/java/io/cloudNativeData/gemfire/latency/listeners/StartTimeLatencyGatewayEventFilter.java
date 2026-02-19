package io.cloudNativeData.gemfire.latency.listeners;

import org.apache.geode.cache.wan.GatewayEventFilter;
import org.apache.geode.cache.wan.GatewayQueueEvent;
import org.apache.geode.pdx.PdxInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Set the start time for a cache update
 * @author gregory green
 */
public class StartTimeLatencyGatewayEventFilter implements GatewayEventFilter {

    private final Logger logger = LogManager.getLogger(this.getClass());
    @Override
    public boolean beforeEnqueue(GatewayQueueEvent gatewayQueueEvent) {

        PdxInstance pdx = (PdxInstance) gatewayQueueEvent.getDeserializedValue();
        logger.debug("Adding startTime to {}",pdx);
        pdx.createWriter().setField("startTime", System.currentTimeMillis());

        return true;
    }

    @Override
    public boolean beforeTransmit(GatewayQueueEvent gatewayQueueEvent) {
        return true;
    }

    @Override
    public void afterAcknowledgement(GatewayQueueEvent gatewayQueueEvent) {

    }
}

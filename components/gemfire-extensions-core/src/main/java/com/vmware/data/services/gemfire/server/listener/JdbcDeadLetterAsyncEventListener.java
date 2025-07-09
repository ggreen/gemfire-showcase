package com.vmware.data.services.gemfire.server.listener;

import com.vmware.data.services.gemfire.client.RegionTemplate;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class JdbcDeadLetterAsyncEventListener implements AsyncEventListener {

    private final Logger log = LogManager.getLogger(JdbcDeadLetterAsyncEventListener.class);
    private final JdbcConsumer consumer;
    private final Region<Object,Object> deadLetterRegion;

    public JdbcDeadLetterAsyncEventListener(JdbcConsumer consumer, Region<Object, Object> deadLetterRegion) {
        this.consumer = consumer;
        this.deadLetterRegion = deadLetterRegion;
    }

    @Override
    public boolean processEvents(List<AsyncEvent> events) {
        try {
            this.consumer.accept(events);
        } catch (SQLIntegrityConstraintViolationException | SQLDataException e) {
            // Dead-letter non-retryable exceptions
            for(AsyncEvent event : events) {
                try {
                    deadLetterRegion.put(event.getKey(), event.getDeserializedValue());
                }
                catch (Exception exception){
                    log.error(exception);
                    return false;
                }
            }
            return true; //confirm successful processing
        }
        catch (Exception e){
            log.error(e);
            return false; //requeue events for retries
        }
        return true; // events process successfully
    }
}

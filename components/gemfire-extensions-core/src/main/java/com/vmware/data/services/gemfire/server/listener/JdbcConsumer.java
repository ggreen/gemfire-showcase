package com.vmware.data.services.gemfire.server.listener;

import org.apache.geode.cache.asyncqueue.AsyncEvent;

import java.sql.SQLException;
import java.util.List;

@FunctionalInterface
public interface JdbcConsumer {
    void accept(List<AsyncEvent> events) throws SQLException;
}

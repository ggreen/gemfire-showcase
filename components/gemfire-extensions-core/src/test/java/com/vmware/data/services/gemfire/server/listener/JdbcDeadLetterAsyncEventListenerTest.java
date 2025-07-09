package com.vmware.data.services.gemfire.server.listener;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcDeadLetterAsyncEventListenerTest {

    @Mock
    private AsyncEvent<?,?> event;
    @Mock
    private JdbcConsumer consumer;

    private List<AsyncEvent> events;

    @Mock
    private Region<Object, Object> deadLetterRegion;

    private JdbcDeadLetterAsyncEventListener subject;

    @BeforeEach
    void setUp() {

        events = List.of(event);
        subject = new JdbcDeadLetterAsyncEventListener(consumer, deadLetterRegion);
    }

    @DisplayName("Given List<AsyncEvent> when SQlException then return true")
    @Test
    void handle_deadLetter() throws SQLException {
        doThrow(SQLDataException.class).when(consumer).accept(any());

        assertThat(subject.processEvents(events)).isTrue();

        verify(deadLetterRegion).put(any(),any());


    }

    @DisplayName("Given List<AsyncEvent> when error then return false")
    @Test
    void handle_runtimeException() throws SQLException {
        doThrow(RuntimeException.class).when(consumer).accept(any());

        assertThat(subject.processEvents(events)).isFalse();

        verify(deadLetterRegion).put(any(),any());


    }

    @DisplayName("Given List<AsyncEvent> when no error then return true")
    @Test
    void handle_success() throws SQLException {

        assertThat(subject.processEvents(events)).isTrue();

        verify(deadLetterRegion,never()).put(any(),any());


    }
}
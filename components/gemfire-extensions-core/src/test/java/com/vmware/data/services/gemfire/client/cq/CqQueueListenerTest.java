package com.vmware.data.services.gemfire.client.cq;

import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.security.user.data.UserProfile;
import org.apache.geode.cache.Operation;
import org.apache.geode.cache.query.CqEvent;
import org.apache.geode.cache.query.CqException;
import org.apache.geode.cache.query.CqQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author gregory green
 *
 */
@ExtendWith(MockitoExtension.class)
class CqQueueListenerTest {

    @Mock
    private CqEvent cqEvent;
    @Mock
    private CqQuery cqQuery;
    private UserProfile expected = JavaBeanGeneratorCreator.of(UserProfile.class).create();
    private CqQueueListener<UserProfile> subject;

    @BeforeEach
    void setUp() {
        subject = new CqQueueListener<UserProfile>();
    }

    @Test
    @DisplayName("Given create event WHEN onEvent THEN queue contains expected")
    void onEvent_create() {

        Operation operation = Operation.CREATE;
        when(cqEvent.getBaseOperation()).thenReturn(operation);
        when(cqEvent.getNewValue()).thenReturn(expected);

        subject.onEvent(cqEvent);

        assertEquals(expected, subject.remove());

    }

    @Test
    @DisplayName("Given delete event WHEN onEvent THEN queue empty")
    void onEvent_ignore_delete() {

        Operation operation = Operation.DESTROY;
        when(cqEvent.getBaseOperation()).thenReturn(operation);


        subject.onEvent(cqEvent);

        assertTrue(subject.isEmpty());
    }

    @DisplayName("When close THEN close CQ")
    @Test
    void close() throws CqException {
        subject.setCqQuery(cqQuery);

        subject.close();

        verify(cqQuery).close();

    }

    @DisplayName("When dispose THEN close CQ")
    @Test
    void dispose() throws CqException {
        subject.setCqQuery(cqQuery);

        subject.dispose();

        verify(cqQuery).close();

    }
}
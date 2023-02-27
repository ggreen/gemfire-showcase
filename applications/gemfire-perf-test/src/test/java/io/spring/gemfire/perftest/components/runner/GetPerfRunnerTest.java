package io.spring.gemfire.perftest.components.runner;

import com.vmware.data.services.gemfire.client.GemFireClient;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.query.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPerfRunnerTest {
    private String regionName = "test";
    private GetPerfRunner subject;

    @Mock
    private SelectResults<Object> results;

    @Mock
    private ClientCache cache;

    @Mock
    private Region<Object,Object> mockRegion = null;

    @Mock
    private QueryService queryService;

    @Mock
    private Query query;

    @Mock
    private Iterator<Object> mockIterator;

    @Mock
    private GemFireClient client;

    @BeforeEach
    void setUp() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {

        when(mockIterator.next()).thenReturn("hello");

        when(results.size()).thenReturn(1);

        when(results.iterator()).thenReturn(mockIterator);

        when(query.execute()).thenReturn(results);

        when(queryService.newQuery(anyString())).thenReturn(query);

        when(client.getClientCache()).thenReturn(cache);
//        when(cache.getRegion(anyString())).thenReturn(mockRegion);
        when(client.getRegion(anyString())).thenReturn(mockRegion);
        when(cache.getQueryService()).thenReturn(queryService);

        subject = new GetPerfRunner(client,regionName);

        subject.init();

    }

    @Test
    void whenRunThenGets() {

        subject.run();
        verify(mockRegion).get(any());
    }
}
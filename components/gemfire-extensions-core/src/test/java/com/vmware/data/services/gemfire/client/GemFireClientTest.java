package com.vmware.data.services.gemfire.client;

import com.vmware.data.services.gemfire.io.QuerierService;
import nyla.solutions.core.util.Config;
import org.apache.geode.cache.AttributesMutator;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.PdxWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * @author Gregory Geen
 */
@ExtendWith(MockitoExtension.class)
public class GemFireClientTest
{
    private GemFireClient subject;

    @Mock
    private ClientCache clientCache;
    @Mock
    private ClientRegionFactory<?, ?> proxyRegionfactory;
    @Mock
    private ClientRegionFactory<?, ?> cachingProxyRegionfactory;
    @Mock
    private Region<?,?> proxyRegion;
    @Mock
    private Region<?,?> cachingProxyRegion;
    @Mock
    private AttributesMutator<?, ?> attributesMutator;
    @Mock
    private QuerierService querier;


    @Test
    void builder()
    {
        assertNotNull(GemFireClient.builder());
    }

    @BeforeEach
    void setUp()
    {

        subject = new GemFireClient(clientCache,proxyRegionfactory,
                cachingProxyRegionfactory,querier);

    }

    @Test
    void getRegionClientCachePoolName()
    {
        when(clientCache.getRegion(anyString())).thenReturn((Region)proxyRegion);

        assertNotNull(subject.getRegion(clientCache,"regionName","myPool"));
    }




}

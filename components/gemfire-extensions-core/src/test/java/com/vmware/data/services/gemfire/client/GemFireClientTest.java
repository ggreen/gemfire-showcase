package com.vmware.data.services.gemfire.client;

import com.vmware.data.services.gemfire.io.QuerierService;
import com.vmware.data.services.gemfire.io.function.FuncExe;
import com.vmware.data.services.gemfire.lucene.TextPageCriteria;
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
import org.junit.jupiter.api.Assertions;
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
    private ClientCache clientCache;
    private ClientRegionFactory<?, ?> proxyRegionfactory;
    private ClientRegionFactory<?, ?> cachingProxyRegionfactory;
    private Region<?,?> proxyRegion;
    private Region<?,?> cachingProxyRegion;
    private AttributesMutator<?, ?> attributesMutator;
    private QuerierService querier;
    @Mock
    private TextPageCriteria criteria;


    @Test
    void builder()
    {
        assertNotNull(GemFireClient.builder());
    }

    @BeforeEach
    void setUp()
    {
        clientCache = mock(ClientCache.class);
        proxyRegionfactory = mock(ClientRegionFactory.class);
        querier = mock(QuerierService.class);

        cachingProxyRegionfactory = mock(ClientRegionFactory.class);
        attributesMutator = mock(AttributesMutator.class);
        when(clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY))
               .thenReturn((ClientRegionFactory)proxyRegionfactory);


        when(proxyRegionfactory.setPoolName(ArgumentMatchers.anyString()))
               .thenReturn((ClientRegionFactory)proxyRegionfactory);
        when(cachingProxyRegionfactory.setPoolName(ArgumentMatchers.anyString()))
               .thenReturn((ClientRegionFactory)cachingProxyRegionfactory);
        cachingProxyRegion = mock(Region.class);
        proxyRegion = mock(Region.class);

        when(proxyRegionfactory.create(ArgumentMatchers.anyString())).thenReturn((Region)proxyRegion);
        when(cachingProxyRegionfactory.create(ArgumentMatchers.anyString())).thenReturn((Region)cachingProxyRegion);
        when(proxyRegion.getAttributesMutator()).thenReturn((AttributesMutator)attributesMutator);
        when(cachingProxyRegion.getAttributesMutator()).thenReturn((AttributesMutator)attributesMutator);

        subject = new GemFireClient(clientCache,proxyRegionfactory,
                cachingProxyRegionfactory,querier);

    }

    @Test
    void getClientCache()
    {
        assertNotNull(subject.getClientCache());
    }

    @Test
    void getQuerierService()
    {
        assertNotNull(subject.getQuerierService());
    }

    @Test
    void getRegionClientCache()
    {
        assertNotNull(GemFireClient.getRegion(clientCache,"regionName"));
    }

    @Test
    void getRegionClientCachePoolName()
    {
        assertNotNull(GemFireClient.getRegion(clientCache,"regionName","myPool"));
    }

    @Test
    public void testConstructSecurity()
    throws Exception
    {
        System.setProperty("SSL_KEYSTORE_CLASSPATH_FILE", "keystore.jks");
        Config.reLoad();
        Properties props = new Properties();

        subject.constructSecurity(props);

        assertTrue(!props.isEmpty());

        assertTrue( props.keySet().stream().anyMatch(k -> k.toString().startsWith("ssl")),"Has ssl");

    }

    @Test
    public void getRegion()
    throws Exception
    {

        Region<Object, Object> region = subject.getRegion("test");
        assertNotNull(region);
        verify(proxyRegionfactory).create(ArgumentMatchers.anyString());
    }

    @Test
    public void testing_get_region_with_loader()
            throws Exception
    {

        CacheLoader<Object,Object> loader = mock(CacheLoader.class);
        Region<Object, Object> region = subject.getRegion("test",loader);

        verify(region).getAttributesMutator();
        verify(attributesMutator).setCacheLoader((CacheLoader)loader);

    }

    public void testWithCachingProxy()
    {

        Region<String, Object> region = subject.getRegion("test");
        region.put("1", "1");

       verify((Region)cachingProxyRegion).put("1","1");

    }

//    @Test
//    public void testPdxSerializer()
//    throws Exception
//    {
//        PdxSerializer pdxSerializer = subject.createPdxSerializer(ReflectionBasedAutoSerializer.class.getName(), ReflectionBasedAutoSerializer.class.getName());
//        System.out.println("pdxSerializier" + pdxSerializer);
//        Assertions.assertNotNull(pdxSerializer);
//        assertTrue(pdxSerializer instanceof ReflectionBasedAutoSerializer);
//
//        String[] pattern = {".*"};
//        PdxSerializer pdxSerializerVerifier = subject.createPdxSerializer(TestPdxSerialzier.class.getName(), pattern);
//
//        assertTrue(pdxSerializerVerifier instanceof TestPdxSerialzier);
//    }

    @Test
    void select()
    {
        String oql = "";
        Collection<Object> results = subject.select(oql);
        assertNotNull(results);
        verify(querier).query(ArgumentMatchers.anyString(),
                nullable(RegionFunctionContext.class));
    }

    @Test
    void selectRegionFunctionContext()
    {
        RegionFunctionContext rfc = mock(RegionFunctionContext.class);
        String oql = "";
        Collection<Object> results = subject.select(oql,rfc);
        assertNotNull(results);

        verify(querier).query(ArgumentMatchers.anyString(), ArgumentMatchers.any(RegionFunctionContext.class));
    }

    @Test
    void searchWithPageKeys_returnsNull() throws Exception
    {
        TextPageCriteria criteria = null;
        FuncExe funcExe = mock(FuncExe.class);
        Assertions.assertNull(subject.searchWithPageKeys(criteria,funcExe));

    }

    @Test
    void searchWithPageKeys() throws Exception
    {
        FuncExe funcExe = mock(FuncExe.class);
        subject.searchWithPageKeys(criteria,funcExe);
        verify(funcExe).exe(ArgumentMatchers.any());
    }

    @Test
    void settingCacheProxy()
    {
        subject.setCachingProxy(true);
        assertTrue(subject.isCachingProxy());

        subject.setCachingProxy(false);
        assertFalse(subject.isCachingProxy());
    }

    @Test
    void registerCacheLoader()
    {
        CacheLoader cacheLoader = mock(CacheLoader.class);
        GemFireClient.registerCacheLoader(proxyRegion,cacheLoader);
        verify(proxyRegion).getAttributesMutator();
        verify(attributesMutator).setCacheLoader(cacheLoader);
    }

    public static class TestPdxSerialzier implements PdxSerializer
    {

        public TestPdxSerialzier(String... args)
        {
        }

        @Override
        public boolean toData(Object o, PdxWriter out)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Object fromData(Class<?> clazz, PdxReader in)
        {
            // TODO Auto-generated method stub
            return null;
        }
    }

}

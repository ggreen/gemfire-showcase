package com.vmware.data.services.apache.geode.client;

import nyla.solutions.core.io.IO;
import nyla.solutions.core.util.Config;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.PoolFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Gregory Green
 */
public class GeodeSettingsTest
{

    @Test
    public void test_connection_builder_with_locators()
    {

        PoolFactory factory1 = mock(PoolFactory.class);
        String locators1 = "host1[1002]";
        GeodeSettings.constructLocators(locators1, factory1);

        verify(factory1, atLeast(1)).addLocator(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());

        PoolFactory factory2 = mock(PoolFactory.class);

        String locators2 = "host1[1002],host[232]";
        GeodeSettings.constructLocators(locators2, factory2);

        verify(factory1, atLeast(1)).addLocator(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());


    }

    @Test
    public void testGetLocatorUrlList()
    throws Exception
    {
        System.setProperty(GeodeConfigConstants.LOCATORS_PROP, "host1[1],host2[2],host2[3];");

        Config.reLoad();

        List<URI> list = GeodeSettings.getInstance().getLocatorUrlList();
        Assertions.assertTrue(list != null && !list.isEmpty());

    }//--------------------------------------

    @Test
    public void testLocatorsBuild()
    throws Exception
    {
        try
        {
            System.setProperty(GeodeConfigConstants.LOCATORS_PROP, "host1[1],host2[2],host2[3]");

            Config.reLoad();

            List<URI> list = GeodeSettings.getInstance().getLocatorUrlList();
            Assertions.assertTrue(list != null && !list.isEmpty());

            Assertions.assertEquals(3, list.size());


            ClientCacheFactory factory = mock(ClientCacheFactory.class);
            GeodeSettings.getInstance().constructPoolLocator(factory);

            Assertions.assertNotNull(factory);
            verify(factory, atLeastOnce()).addPoolLocator(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());
            verify(factory, times(3)).addPoolLocator(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());


        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }//----------------------------------

    @Test
    public void testGetInstance()
    throws Exception
    {


        GeodeSettings config = GeodeSettings.getInstance();

        Assertions.assertNotNull(config);

    }//------------------------------------------------

    @Test
    public void testGetLocators()
    throws Exception
    {
        String envContent = IO.readClassPath("json/vcap.json");
        GeodeSettings config = new GeodeSettings(envContent);

        String locators = config.getLocators();
        System.out.println("locators:" + locators);
        Assertions.assertNotNull(locators);
        Assertions.assertEquals("10.244.0.4[55221],10.244.1.2[55221],10.244.0.130[55221]", locators);
    }//------------------------------------------------

    @Test
    public void testBlankEnvContent()
    throws Exception
    {
        String envContent = null;
        GeodeSettings config = new GeodeSettings(envContent);

        String envLocatorHost = System.getenv(GeodeConfigConstants.LOCATOR_HOST_PROP);

        if (envLocatorHost == null || envLocatorHost.length() == 0)
        {
            List<URI> l = config.getLocatorUrlList();
            assertTrue(
                    l == null || l.size() == 0,"getLocatorHost:" + config.getLocatorUrlList() + " would be empty");


            envContent = " ";
            config = new GeodeSettings(envContent);

            l = config.getLocatorUrlList();
            Assertions.assertTrue(l == null || l.size() == 0,"getLocatorHost:" + l + " would be empty");

        }
        else
        {
            assertEquals(envLocatorHost, GeodeSettings.getInstance().getLocatorHost());
        }

    }//------------------------------------------------

    @Test
    public void testGetUsername()
    throws Exception
    {
        String envContent = IO.readClassPath("json/vcap.json");
        GeodeSettings config = new GeodeSettings(envContent);

        String token = null;
        Assertions.assertNull(config.getSecuredToken("invalid", token));
        assertEquals("developer", config.getSecuredToken("developer", token).getName());
        assertEquals("cluster_operator", config.getSecuredToken("cluster_operator", token).getName());

        Assertions.assertNotNull(config.getSecuredToken(null));
    }//------------------------------------------------

    /**
     * Test Get password
     *
     * @throws Exception the exception
     */
    @Test
    public void testGetPassword()
    throws Exception
    {
        String envContent = IO.readClassPath("json/vcap.json");
        GeodeSettings config = new GeodeSettings(envContent);

        String token = null;
        assertArrayEquals("some_developer_password".toCharArray(), config.getSecuredToken("developer", token).getCredentials());
        assertArrayEquals("some_password".toCharArray(), config.getSecuredToken("cluster_operator", token).getCredentials());

    }

}

package com.vmware.data.services.apache.geode.client;

import org.apache.geode.cache.client.PoolFactory;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for PoolFactoryBuilder
 * @author Gregory Green
 */
public class PoolFactoryBuilderTest
{
    @Test
    public void test_building_factory()
    {
        PoolFactory f = mock(PoolFactory.class);
        PoolFactoryBuilder b = new PoolFactoryBuilder(f);

        b.addHostPort("host",10034);
        b.addHostPort("host2",10034);

        verify(f, atLeast(2)).addLocator(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());

    }
}
package com.vmware.data.services.gemfire.client;

import org.apache.geode.cache.client.PoolFactory;

/**
 * @author Gregory Green
 */
public class PoolFactoryBuilder implements ConnectionBuilder
{
    private final PoolFactory factory;

    /**
     *
     * @param factory the pool factory
     */
    public PoolFactoryBuilder(PoolFactory factory)
    {
        this.factory = factory;
    }

    public void addHostPort(String host, int port)
    {
        this.factory.addLocator(host,port);
    }
}

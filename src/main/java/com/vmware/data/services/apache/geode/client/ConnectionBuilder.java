package com.vmware.data.services.apache.geode.client;

/**
 * Connection builder abstraction
 * @author Gregory Green
 */
public interface ConnectionBuilder
{
    public void addHostPort(String host, int port);
}

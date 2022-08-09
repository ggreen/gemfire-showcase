package com.vmware.data.services.gemfire.client;

/**
 * Connection builder abstraction
 * @author Gregory Green
 */
public interface ConnectionBuilder
{
    public void addHostPort(String host, int port);
}

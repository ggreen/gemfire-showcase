package com.vmware.data.services.gemfire.client;


/**
 * GemFireClientBuilder for an GemFire connection
 * @author Gregory Green
 */
public interface Builder
{
    /**
     *
     * @param locators format host[port],host[port]
     * @return the builder instance
     */
    Builder locators(String locators);

    /**
     *
     * @param clientName the client name
     * @return the builder instance
     */
    Builder clientName(String clientName);

    Builder userName(String userName);

    Builder password(char[] password);


    GemFireClient build();
}

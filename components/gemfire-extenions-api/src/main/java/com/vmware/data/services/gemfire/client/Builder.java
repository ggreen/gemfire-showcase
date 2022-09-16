package com.vmware.data.services.gemfire.client;


/**
 * GemFireClientBuilder
 *
 * @author Gregory Green
 */
public interface Builder
{
    Builder locators(String locators);

    Builder clientName(String clientName);

    Builder userName(String userName);

    Builder password(char[] password);


    GemFireClient build();
}

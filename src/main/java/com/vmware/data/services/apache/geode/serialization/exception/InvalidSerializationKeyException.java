package com.vmware.data.services.apache.geode.serialization.exception;

import java.io.Serializable;

/**
 * @author Gregory Green
 */
public class InvalidSerializationKeyException extends RuntimeException
{
    public <Key extends Serializable> InvalidSerializationKeyException(Key key)
    {
        super(key.getClass().getName()+" must be a native Java JDK object");
    }
}

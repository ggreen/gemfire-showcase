package com.vmware.data.services.apache.geode.serialization;

import com.vmware.data.services.apache.geode.serialization.exception.InvalidSerializationKeyException;
import nyla.solutions.core.operations.ClassPath;
import org.apache.geode.pdx.PdxInstance;
import java.io.Serializable;
import java.util.Objects;

/**
 * Wrapper for serialization PDX entry to data migration
 * support using JSON.
 *
 * @author Gregory Green
 */
public class SerializationPdxEntryWrapper<Key extends Serializable>
{
    private String keyClassName;
    private String valueClassName;
    private String keyString; //
    private String valueJson;
    private final PDX pdx;

    public SerializationPdxEntryWrapper(PDX pdx)
    {
        this.pdx = pdx;
    }

    public SerializationPdxEntryWrapper()
    {
        pdx = new PDX();
    }
    protected SerializationPdxEntryWrapper(Key key,String valueClassName, PdxInstance pdxInstance)
    {
        if(key == null)
            throw new IllegalArgumentException("key required");

        if(isCustom(key))
            throw new InvalidSerializationKeyException(key);

        this.pdx = new PDX();
        this.keyClassName = key.getClass().getName();

        this.valueClassName = valueClassName;

        this.valueJson = pdx.toJSON(pdxInstance,this.valueClassName);
        this.keyString = key.toString();

    }
    public String getKeyString()
    {
        return keyString;
    }

    public void setKeyString(String keyString)
    {
        this.keyString = keyString;
    }

    public String getValueJson()
    {
        return valueJson;
    }

    public void setValueJson(String valueJson)
    {
        if(valueJson != null && valueJson.length() > 0)
            pdx.validateJson(valueJson);

        this.valueJson = valueJson;
    }//-------------------------------------------


    public String getKeyClassName()
    {
        return keyClassName;
    }//-------------------------------------------

    public void setKeyClassName(String keyClassName)
    {
        this.keyClassName = keyClassName;
    }//-------------------------------------------

    protected static boolean isCustom(Object key)
    {
        if(key == null)
            return false;

        String className = key.getClass().getName();

        return !className.matches("(float|char|short|double|int|long|byte|boolean|(java.*))");
    }//-------------------------------------------

    public Key deserializeKey()
    {
       return ClassPath.newInstance(this.keyClassName,this.keyString);
    }//-------------------------------------------

    public PdxInstance toPdxInstance()
    {
        return pdx.fromJSON(this.valueJson);
    }//-------------------------------------------

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerializationPdxEntryWrapper<?> that = (SerializationPdxEntryWrapper<?>) o;
        return Objects.equals(keyClassName, that.keyClassName) &&
                Objects.equals(keyString, that.keyString) &&
                Objects.equals(valueJson, that.valueJson);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(keyClassName, keyString, valueJson);
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("SerializationPdxEntryWrapper{");
        sb.append("keyClassName='").append(keyClassName).append('\'');
        sb.append(", keyString='").append(keyString).append('\'');
        sb.append(", valueJson='").append(valueJson).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

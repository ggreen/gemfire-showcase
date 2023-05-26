package com.vmware.data.services.gemfire.serialization;

import nyla.solutions.core.operations.ClassPath;
import org.apache.geode.json.JsonDocument;

import java.io.Serializable;
import java.util.Objects;

/**
 * Wrapper for serialization PDX entry to data migration
 * support using JSON.
 *
 * @author Gregory Green
 */
public class SerializationJsonEntryWrapper<Key extends Serializable>
{
    private  final String keyClassName;
    private  final String valueClassName;
    private final String keyString; //
    private  final String valueJson;


    protected SerializationJsonEntryWrapper(Key key, String valueClassName, JsonDocument pdxInstance)
    {
        if(key == null)
            throw new IllegalArgumentException("key required");

        if(isCustom(key))
            throw new IllegalArgumentException(key+" does not match class types of float|char|short|double|int|long|byte|boolean|(java.*)");

        this.keyClassName = key.getClass().getName();

        this.valueClassName = valueClassName;

        this.valueJson = pdxInstance.toJson();
        this.keyString = key.toString();

    }

    public String getKeyString()
    {
        return keyString;
    }

    public String getValueJson()
    {
        return valueJson;
    }

    public String getKeyClassName()
    {
        return keyClassName;
    }


    protected static boolean isCustom(Object key)
    {
        if(key == null)
            return false;

        String className = key.getClass().getName();

        return !className.matches("(float|char|short|double|int|long|byte|boolean|(java.*))");
    }

    public Key deserializeKey()
    {
       return ClassPath.newInstance(this.keyClassName,this.keyString);
    }

    public JsonDocument toJsonDocument()
    {
        return GemFireJson.createPdx().fromJSON(this.valueJson);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerializationJsonEntryWrapper<?> that = (SerializationJsonEntryWrapper<?>) o;
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

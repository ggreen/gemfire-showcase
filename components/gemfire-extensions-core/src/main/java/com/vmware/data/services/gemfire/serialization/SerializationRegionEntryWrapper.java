package com.vmware.data.services.gemfire.serialization;

/**
 * <pre>
 *  Wrapper for regions key/values to serialize to JSON.
 *  The object adds to the key/value class names to the JSON text output.
 *  These addition attributes helps with de-serialization.
 *  </pre>
 * @author Gregory Green
 *
 */
public class SerializationRegionEntryWrapper
{
	private Object value;
	private String valueClassName;
	private Object key;
	private String keyClassName;
	
	public SerializationRegionEntryWrapper(Object key, Object value)
	{
		this(key, key.getClass().getName(), value, value.getClass().getName());
	}// --------------------------------------------------------

	public SerializationRegionEntryWrapper(
			Object key, String keyClassName,
			Object value, String valueClassName)
	{
		super();
		this.value = value;
		this.valueClassName = valueClassName;
		this.key = key;
		this.keyClassName = keyClassName;
	}
	/**
	 * @return the value
	 */
	public Object getValue()
	{
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
	/**
	 * @return the valueClassName
	 */
	public String getValueClassName()
	{
		return valueClassName;
	}
	/**
	 * @param valueClassName the valueClassName to set
	 */
	public void setValueClassName(String valueClassName)
	{
		this.valueClassName = valueClassName;
	}
	/**
	 * @return the key
	 */
	public Object getKey()
	{
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(Object key)
	{
		this.key = key;
	}
	/**
	 * @return the keyClassName
	 */
	public String getKeyClassName()
	{
		return keyClassName;
	}
	/**
	 * @param keyClassName the keyClassName to set
	 */
	public void setKeyClassName(String keyClassName)
	{
		this.keyClassName = keyClassName;
	}
	
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("SerializationWrapper [value=").append(value)
				.append(", valueClassName=").append(valueClassName)
				.append(", key=").append(key).append(", keyClassName=")
				.append(keyClassName).append("]");
		return builder.toString();
	}


}

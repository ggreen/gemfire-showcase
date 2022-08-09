package com.vmware.data.services.gemfire.serialization;

/**
 * <pre>
 *  Wrapper for Nest Map object's for serialize to JSON.
 *  The object adds to the key class names to the JSON text output.
 *  These addition attributes helps with de-serialization of the Map key that may not be strings.
 *  </pre>
 *  
 * @author Gregory Green
 *
 */
public class
SerializationMapKeyWrapper
{
	
	public SerializationMapKeyWrapper()
	{}

	
	public SerializationMapKeyWrapper(Object key)
	{
		this(key, key.getClass().getName());
	}// --------------------------------------------------------


	public SerializationMapKeyWrapper(
			Object key, String keyClassName)
	{

		this.key = key;
		this.keyClassName = keyClassName;
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
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((keyClassName == null) ? 0 : keyClassName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SerializationMapKeyWrapper other = (SerializationMapKeyWrapper) obj;
		if (key == null)
		{
			if (other.key != null)
				return false;
		} else
			if (!key.equals(other.key))
				return false;
		if (keyClassName == null)
		{
			if (other.keyClassName != null)
				return false;
		} else
			if (!keyClassName.equals(other.keyClassName))
				return false;
		return true;
	}

	@Override
	public String toString()
	{
		return String.format(
				"SerializationMapKeyWrapper [key=%s, keyClassName=%s]", key,
				keyClassName);
	}



	private Object key;
	private String keyClassName;
}

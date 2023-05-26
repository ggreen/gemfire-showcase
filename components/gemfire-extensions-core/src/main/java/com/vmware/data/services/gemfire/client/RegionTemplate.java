package com.vmware.data.services.gemfire.client;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.CacheWriterException;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.TimeoutException;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;

import com.vmware.data.services.gemfire.io.GemFireIO;
import com.vmware.data.services.gemfire.io.function.GetAllRegionValuesFunction;

/**
 * 
 * @author Gregory Green
 *
 * @param <K> the region key type
 * @param <V> the value type
 */
public class RegionTemplate<K,V>
{	
	public RegionTemplate(Region<K, V> region)
	{
		this.region = region;
	}//------------------------------------------------

	/**
	 * @param key the entry key
	 * @return the entry value
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public V get(Object key)
	{
		return region.get(key);
	}



	/**
	 * @param key the region key
	 * @param aCallbackArgument the call object
	 * @return the region value
	 * @throws TimeoutException when a timeout error occurs
	 * @throws CacheLoaderException  when a timeout error occurs
	 * @see org.apache.geode.cache.Region#get(java.lang.Object, java.lang.Object)
	 */
	public V get(Object key, Object aCallbackArgument) throws TimeoutException, CacheLoaderException
	{
		return region.get(key, aCallbackArgument);
	}



	/**
	 * @param key the region
	 * @param value the new value to put
	 * @return replaced value
	 * @throws TimeoutException when timed connection
	 * @throws CacheWriterException write GemFire issues occur
	 * @see org.apache.geode.cache.Region#put(java.lang.Object, java.lang.Object)
	 */
	public V put(K key, V value) throws TimeoutException, CacheWriterException
	{
		return region.put(key, value);
	}



	/**
	 * @param key the key
	 * @param value the region value
	 * @param aCallbackArgument the call back argument
	 * @return the replace value
	 * @throws TimeoutException when timed connection
	 * 	 * @throws CacheWriterException write GemFire issues occur
	 * @see org.apache.geode.cache.Region#put(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public V put(K key, V value, Object aCallbackArgument) throws TimeoutException, CacheWriterException
	{
		return region.put(key, value, aCallbackArgument);
	}



	/**
	 * @return  key set
	 * @see org.apache.geode.cache.Region#keySet()
	 */
	public Set<K> keySet()
	{
		return region.keySetOnServer();
	}



	/**
	 * @param map the map
	 * @see org.apache.geode.cache.Region#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends K, ? extends V> map)
	{
		region.putAll(map);
	}



	/**
	 * @param map the pull all
	 * @param aCallbackArgument the call back argument
	 * @see org.apache.geode.cache.Region#putAll(java.util.Map, java.lang.Object)
	 */
	public void putAll(Map<? extends K, ? extends V> map, Object aCallbackArgument)
	{
		region.putAll(map, aCallbackArgument);
	}



	/**
	 * @param keys the keys to remove
	 * @see org.apache.geode.cache.Region#removeAll(java.util.Collection)
	 */
	public void removeAll(Collection<? extends K> keys)
	{
		region.removeAll(keys);
	}



	/**
	 * @param keys the keys to from
	 * @param aCallbackArgument the call argument
	 * @see org.apache.geode.cache.Region#removeAll(java.util.Collection, java.lang.Object)
	 */
	public void removeAll(Collection<? extends K> keys, Object aCallbackArgument)
	{
		region.removeAll(keys, aCallbackArgument);
	}



	/**
	 * @param keys the key entries to get
	 * @return the map of match entries
	 * @see org.apache.geode.cache.Region#getAll(java.util.Collection)
	 */
	public Map<K, V> getAll(Collection<?> keys)
	{
		return region.getAll(keys);
	}

	/**
	 * @param keys the key entries to get
	 * @return the map of match entries
	 * @throws Exception when an unknown internal error occurs
	 * @see org.apache.geode.cache.Region#getAll(java.util.Collection)
	 */
	@SuppressWarnings({ "unchecked" })
	public Collection<V> getAllValues(Collection<K> keys)
	throws Exception
	{
		if(keys ==  null || keys.isEmpty())
			return null;
		
		try
		{
			if(DataPolicy.EMPTY.equals(this.region.getAttributes().getDataPolicy()))
			{
				//execute thru function
				Execution<K,?,?> exe = FunctionService.onRegion(this.region).setArguments(keys);
				
				Collection<V> results = GemFireIO.exeWithResults(exe, new GetAllRegionValuesFunction<K>());
				
				return results;
			}
			else
			{
				Map<K,V> map = region.getAll(keys);
				
				if(map == null || map.isEmpty())
					return null;
				
				return map.values();
			}
		}
		catch (RuntimeException e)
		{
			throw e;
		}	
	}//------------------------------------------------

	/**
	 * @param <T> the type key
	 * @param keys the keys
	 * @param aCallbackArgument the callback argument
	 * @return Map the map entries matching the keys
	 * @see org.apache.geode.cache.Region#getAll(java.util.Collection, java.lang.Object)
	 */
	public <T extends K> Map<T, V> getAll(Collection<T> keys, Object aCallbackArgument)
	{
		return region.getAll(keys, aCallbackArgument);
	}//------------------------------------------------



	/**
	 * @param key the key of the entry to remove
	 * @return the value
	 * @see org.apache.geode.cache.Region#remove(java.lang.Object)
	 */
	public V remove(Object key)
	{
		return region.remove(key);
	}



	/**
	 * @return the key set on server
	 * @see org.apache.geode.cache.Region#keySetOnServer()
	 */
	public Set<K> keySetOnServer()
	{
		return region.keySetOnServer();
	}



	/**
	 * @param key the key of the region
	 * @param value the region value
	 * @return the previous entry
	 * @see org.apache.geode.cache.Region#putIfAbsent(java.lang.Object, java.lang.Object)
	 */
	public V putIfAbsent(K key, V value)
	{
		return region.putIfAbsent(key, value);
	}



	/**
	 * @param key the key to remove
	 * @param value the region value
	 * @return boolean if the entry is removed
	 * @see org.apache.geode.cache.Region#remove(java.lang.Object, java.lang.Object)
	 */
	public boolean remove(Object key, Object value)
	{
		return region.remove(key, value);
	}

	/**
	 * @return the region
	 */
	public Region<K, V> getRegion()
	{
		return region;
	}
	
	private final Region<K,V> region;
}

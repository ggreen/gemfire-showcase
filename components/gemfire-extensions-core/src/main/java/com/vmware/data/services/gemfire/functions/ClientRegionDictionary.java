package com.vmware.data.services.gemfire.functions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

/**
 * Use the client cache to obtain region objects
 * @author Gregory Green
 *
 */
public class ClientRegionDictionary implements RegionDictionary
{

	public <K, V> Region<K, V> getRegion(String name)
	{
		ClientCache cache = ClientCacheFactory.getAnyInstance();
		
		
		
		return cache.getRegion(name);
		
	}

}

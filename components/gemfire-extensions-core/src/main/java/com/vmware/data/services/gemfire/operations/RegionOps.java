package com.vmware.data.services.gemfire.operations;

import java.util.Set;

import org.apache.geode.cache.Region;

public class RegionOps
{
	/**
	 * 
	 * @param region the region
	 */
	public static void clearAll(Region<?,?> region)
	{
		Set<?> set = region.keySetOnServer();
		
		//TODO: would be nice to have a bulk remove method
		set.stream().forEach(k ->  region.remove(k)); 
	}//------------------------------------------------
	
	
	public static boolean isEmpty(Region<?,?> region)
	{
		return size(region) == 0;
	}//------------------------------------------------
	
	public static int size(Region<?,?> region)
	{
		Set<?> set = region.keySetOnServer();
		if(set == null || set.isEmpty())
			return 0;
		
		return set.size();
	}//------------------------------------------------
	
}

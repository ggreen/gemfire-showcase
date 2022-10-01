package com.vmware.data.services.gemfire.functions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;

public class FuncAssistant
{

	public static <K,V> Region<K,V> getLocalPrimaryData(Region<K,V> region,RegionFunctionContext rfc)
	{
		if(rfc != null && JvmRegionFunctionContext.class.isAssignableFrom(rfc.getClass()))
		{
			return region;
		}
		else
		{
			return PartitionRegionHelper.getLocalPrimaryData(region);
		}
	}
    
}

package com.vmware.pivotal.labs.services.dataTx.geode.office;


import com.vmware.pivotal.labs.services.dataTx.geode.office.stats.statInfo.*;

public class StatsUtil
{

	public static String formatMachine(String machine)
	{

		return machine;
	}
	/**
	 * Current supports get cache server name
	 * Determine the logic name of app
	 * @param resources the resource used to determine namne
	 * @return the application name (ex: datanode or locator name)
	 */
	public static String getAppName(ResourceInst[] resources)
	{
		if(resources == null || resources.length == 0)
			return null;
		
		ResourceType rt = null;
		for (ResourceInst resourceInst : resources)
		{
			if(resourceInst == null)
				continue;
			
			rt = resourceInst.getType();
			if(rt == null)
				continue;
			
			if(!"CacheServerStats".equals(rt.getName()))
				continue;
			
			return resourceInst.getName();
		}
		return null;
	}


}

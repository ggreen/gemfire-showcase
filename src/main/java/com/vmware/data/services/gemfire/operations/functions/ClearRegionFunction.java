package com.vmware.data.services.gemfire.operations.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.EntryNotFoundException;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.security.ResourcePermission;
import org.apache.geode.security.ResourcePermission.Operation;
import org.apache.geode.security.ResourcePermission.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Deletes all entries in a given region
 * @author Gregory Green
 *
 */
public class ClearRegionFunction implements Function<Object>, Declarable
{

	@Override
	public void init(Properties arg0)
	{
	}

	@Override
	public void execute(FunctionContext<Object> functionContext)
	{
		Logger logWriter = LogManager.getLogger(ClearRegionFunction.class);
		
		if(!(functionContext instanceof RegionFunctionContext))
		{
			throw new IllegalArgumentException("onRegion execution required");
		}
		
		ResultSender<Integer> sender = functionContext.getResultSender();
		
		RegionFunctionContext rfc = (RegionFunctionContext)functionContext;
		
		Region<Object,Object> region = rfc.getDataSet();
		
		if(region.getAttributes().getDataPolicy().withPartitioning())
		{
			region = PartitionRegionHelper.getLocalData(region);			
		}
		
		logWriter.warn("Executing "+this.getClass().getName()+"  for region:"+region.getFullPath());
		
		Set<Object> keySet = region.keySet();

		
		if(keySet == null || keySet.isEmpty())
		{
			sender.lastResult(0);
			return;
		}

		int entriesCount = keySet.size();

		final Region<Object,Object> localRegion = region;

		keySet.parallelStream().forEach(key -> {

			try
			{
				localRegion.invalidate(key);
				localRegion.destroy(key);
			}
			catch(EntryNotFoundException e)
			{
				logWriter.warn("Key:"+key+" ERROR:"+e);
			}
		});

		sender.lastResult(entriesCount);

	}// --------------------------------------------------------

	@Override
	public String getId()
	{
		return "ClearRegionFunction";
	}

	@Override
	public boolean hasResult()
	{
		return true;
	}

	@Override
	public boolean isHA()
	{
		return false;
	}

	@Override
	public boolean optimizeForWrite()
	{
		return false;
	}

	@Override
	public  Collection<ResourcePermission> getRequiredPermissions(
			String regionName) {
	  
		ArrayList<ResourcePermission> requiredPermissions = new ArrayList<ResourcePermission>(2);
		
		requiredPermissions.add(new ResourcePermission(Resource.DATA, Operation.READ, regionName));
		requiredPermissions.add(new ResourcePermission(Resource.DATA, Operation.WRITE, regionName));
		
		return requiredPermissions;

	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 2608607529152147249L;
}

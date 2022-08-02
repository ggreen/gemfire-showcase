package com.vmware.data.services.apache.geode.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.geode.cache.EntryNotFoundException;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.security.ResourcePermission;
import org.apache.geode.security.ResourcePermission.Operation;
import org.apache.geode.security.ResourcePermission.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DemoSecuredFunction implements Function<Object>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// protected LogWriter securityLogger =null;
	Logger logger = LogManager.getLogger(DemoSecuredFunction.class);

	@SuppressWarnings("unchecked")
	@Override
	public void execute(FunctionContext<Object> fc)
	{

		logger.info("************************************");
		if (!(fc instanceof RegionFunctionContext))
		{
			throw new FunctionException("Use FunctionService.onRegion to invoke this function.");
		}
		RegionFunctionContext context;
		try
		{
			context = (RegionFunctionContext) fc;
			Region<Object, Object> RegionData = PartitionRegionHelper.getLocalDataForContext(context);
			Set<Object> keys = new HashSet<Object>(RegionData.keySet());
			// securityLogger.fine("Clearing keys of size "+keys.size() + " For
			// region +"+RegionData.getName());
			for (final Object key : keys)
			{
				try
				{
					RegionData.invalidate(key);
					RegionData.destroy(key);
				}
				catch (EntryNotFoundException e)
				{
					// LOGGER.error("ignoring entry not found exception" + e);
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Remove Partiion Functiona Failure" + e);
			throw new FunctionException("Remove Partion Functiona Failure", e);
		}

		context.getResultSender().lastResult(
		null);
		// logger.info("Successfully Clearerd keys");
	}

	@Override
	public Collection<ResourcePermission> getRequiredPermissions(
	String regionName)
	{
		ArrayList<ResourcePermission> requiredPermissions = new ArrayList<ResourcePermission>(2);
		
		requiredPermissions.add(new ResourcePermission(Resource.DATA, Operation.READ, regionName));
		requiredPermissions.add(new ResourcePermission(Resource.DATA, Operation.WRITE, regionName));
		
		return requiredPermissions;
	}

	@Override
	public String getId()
	{
		return "RemovePartitionFunction";
	}
}

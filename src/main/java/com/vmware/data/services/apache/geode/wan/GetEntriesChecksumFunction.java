package com.vmware.data.services.apache.geode.wan;



import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.pdx.PdxInstance;
import org.apache.logging.log4j.LogManager;

import nyla.solutions.core.security.MD;
import nyla.solutions.core.util.Debugger;


/**
 * Get Entries Checksum for a given region
 * @author Gregory Green
 *
 */
public class GetEntriesChecksumFunction implements Function<Object>, Declarable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3967250792473591101L;

	/**
	 * This function gets HashMap key=Serializable value=BigInteger
	 * 
	 * @param functionContext contains an argument String[] where the first contains the regionName
	 * 
	 */
	@Override
	public void execute(FunctionContext<Object> functionContext)
	{
		try
		{
			String[] args = (String[])functionContext.getArguments();
			if(args == null || args.length == 0)
				throw new IllegalArgumentException("region argument required");
			
			String regionName = args[0];
			
			if(regionName == null || regionName.length() == 0)
				throw new IllegalArgumentException("region name argument required");
			
			Region<Serializable,Object> region = CacheFactory.getAnyInstance().getRegion(regionName);
			
			if(region == null)
				throw new IllegalArgumentException("region:"+regionName+" not found");
			
			functionContext.getResultSender().lastResult(buildCheckSumMap(region));
		}
		catch (Exception e)
		{
			String stack = Debugger.stackTrace(e);
			LogManager.getLogger(getClass()).error(stack);
			throw new FunctionException(stack);
		}
	}// --------------------------------------------------------
	/**
	 * Build check sum map
	 * @param region the region to build the information
	 * @return the map of the keys/checksums
	 */
	HashMap<Serializable,BigInteger> buildCheckSumMap(Region<Serializable,Object> region)
	{
		
		if(region.getAttributes().getDataPolicy().withPartitioning())
		{
			region = PartitionRegionHelper.getLocalData(region);	
		}
		
		Set<Serializable> keySet = region.keySet();
		
		if(keySet == null || keySet.isEmpty())
			return null;
		
		HashMap<Serializable,BigInteger> regionCheckSumMap = new HashMap<Serializable,BigInteger>(keySet.size());
		Object object = null;
		
		Object tmp = null;
		for (Map.Entry<Serializable,Object> entry :region.entrySet())
		{
			object = entry.getValue();
			
			if(PdxInstance.class.isAssignableFrom(object.getClass()))
			{
				tmp = ((PdxInstance)object).getObject();
				
				if(Serializable.class.isAssignableFrom(tmp.getClass()))
				{
					object = tmp;
				}
				//else use PdxInstance.hashCode
			}

			if(!(PdxInstance.class.isAssignableFrom(object.getClass())))
			{
				regionCheckSumMap.put(entry.getKey(), MD.checksum(object));	
			}
			else
			{
				regionCheckSumMap.put(entry.getKey(),  BigInteger.valueOf(object.hashCode()));	
			}
		}
		
		return regionCheckSumMap;
	}// --------------------------------------------------------

	@Override
	public String getId()
	{
		return "GetEntriesChecksumFunction";
	}// --------------------------------------------------------
	@Override
	public boolean hasResult()
	{
		return true;
	}

	@Override
	public boolean isHA()
	{
		return true;
	}// --------------------------------------------------------

	@Override
	public boolean optimizeForWrite()
	{
		return false;
	}// --------------------------------------------------------

	@Override
	public void init(Properties arg0)
	{	
	}// --------------------------------------------------------
}

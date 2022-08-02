package com.vmware.data.services.apache.geode.io.function;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;

@SuppressWarnings("serial")
public class GetAllRegionValuesFunction<V> implements Function<V>
{

	@SuppressWarnings("unchecked")
	@Override
	public void execute(FunctionContext<V> context)
	{
		if(!(context instanceof RegionFunctionContext))
			throw new FunctionException("Execution onRegion required");
		
		RegionFunctionContext rfc = (RegionFunctionContext)context;
		
		Object args = rfc.getArguments();
		if(!(args instanceof Collection))
			throw new FunctionException("Arguments with collection of keys required"); 
			
		Collection<Object> keys = (Collection<Object>)args;
		
		Region<?,V> region =  PartitionRegionHelper.getLocalDataForContext(rfc);
		
		if(keys == null ||keys.isEmpty())
		{
			rfc.getResultSender().lastResult(null);
			return;
		}
			
		ArrayList<Object> list = new ArrayList<>(keys.size());
			
		Object value = null;
		for (Object key : keys)
		{
			value = region.get(key);
			if(value == null)
				continue;
			
			list.add(value);
		}
		
		rfc.getResultSender().lastResult(list);
	}//------------------------------------------------
	
	@Override
	public boolean optimizeForWrite()
	{
		return false;
	}//------------------------------------------------
	
	@Override
	public String getId()
	{
		return "GetAllRegionValuesFunction";
	}

}

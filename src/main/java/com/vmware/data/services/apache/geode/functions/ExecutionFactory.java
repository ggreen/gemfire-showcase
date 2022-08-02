package com.vmware.data.services.apache.geode.functions;


import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;

public interface ExecutionFactory
{
	
	Execution<?,?,?> onRegion(Region<?,?> region);

}

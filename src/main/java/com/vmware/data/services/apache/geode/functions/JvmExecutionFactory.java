package com.vmware.data.services.apache.geode.functions;


import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;

/**
 * @author Gregory Green
 *
 */
public class JvmExecutionFactory implements ExecutionFactory
{

	@Override
	public Execution<?,?,?> onRegion(Region<?, ?> region)
	{
		
		JvmExecution<?,?,?> exe = new JvmExecution<Object,Object,Object>(region);
		
		return exe;
	}

}

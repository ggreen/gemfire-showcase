package com.vmware.data.services.gemfire.functions;


import java.io.Serializable;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;

public class FunctionServiceExecutionFactory implements ExecutionFactory, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3157567950099427294L;

	@Override
	public Execution<?,?,?> onRegion(Region<?, ?> region)
	{
		return FunctionService.onRegion(region);
	}

}

package com.vmware.data.services.apache.geode.functions;


import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.distributed.DistributedMember;

/**
 * @author Gregory Green
 * @param <T> the input type
 * @param <S> the output type
 *
 */
public class JvmResultCollector<T, S> implements ResultCollector<T, S>
{
	@SuppressWarnings("rawtypes")
	public JvmResultCollector(JvmResultsSender resultSender)
	{
		this.sender = resultSender;
	}//-------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public S getResult() throws FunctionException
	{
		return (S)this.sender.getResults();
	}

	@SuppressWarnings("unchecked")
	public S getResult(long paramLong, TimeUnit paramTimeUnit) throws FunctionException, InterruptedException
	{
		return (S)sender.getResults();
	}

	public void addResult(DistributedMember paramDistributedMember, T paramT)
	{
		
	}

	public void endResults()
	{
		
	}

	public void clearResults()
	{
	}

	@SuppressWarnings("rawtypes")
	private JvmResultsSender sender = new JvmResultsSender();

}

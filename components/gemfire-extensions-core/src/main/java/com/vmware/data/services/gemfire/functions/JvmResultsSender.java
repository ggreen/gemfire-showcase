package com.vmware.data.services.gemfire.functions;


import java.util.ArrayList;

import org.apache.geode.cache.execute.ResultSender;

public class JvmResultsSender<T> implements ResultSender<T>
{

	public void sendResult(T result)
	{
		results.add(result);
		
	}//-------------------------------------------------------------------

	public void lastResult(T result)
	{
		results.add(result);
		
	}//-------------------------------------------------------------------

	public void sendException(Throwable paramThrowable)
	{
		throw new RuntimeException(paramThrowable);
	}//-------------------------------------------------------------------

	/**
	 * @return the results
	 */
	public ArrayList<T> getResults()
	{
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(ArrayList<T> results)
	{
		this.results = results;
	}

	private ArrayList<T> results = new ArrayList<T>();
}

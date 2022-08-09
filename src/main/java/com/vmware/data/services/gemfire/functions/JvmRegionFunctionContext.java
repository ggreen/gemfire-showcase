package com.vmware.data.services.gemfire.functions;

import java.util.Set;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;

public class JvmRegionFunctionContext<K, V,T> implements RegionFunctionContext
{
	public JvmRegionFunctionContext()
	{
		this(CacheFactory.getAnyInstance());
	}

	public JvmRegionFunctionContext(Cache cache)
	{
		this.cache = cache;
	}

	public JvmRegionFunctionContext(Region<K,V> dataSet,ResultSender<T> resultSender, Object arguments, Set<?> filter)
	{
		this(CacheFactory.getAnyInstance(),dataSet,resultSender,arguments,filter);
		
	}
	public JvmRegionFunctionContext(Cache cache, Region<K,V> dataSet,ResultSender<T> resultSender, Object arguments, Set<?> filter)
	{
		this.dataSet = dataSet;
		this.resultSender = resultSender;
		this.filter = filter;
		this.arguments = arguments;
		this.cache = cache;
	}//-------------------------------------------------------------------
	
	public Object getArguments()
	{
		return this.arguments;
	}

	public String getFunctionId()
	{
		return this.functionId;
	}

	public ResultSender<T> getResultSender()
	{
		return this.resultSender;
	}

	public boolean isPossibleDuplicate()
	{
		return false;
	}

	public Set<?> getFilter()
	{
		return filter;
	}

	@SuppressWarnings("unchecked")
	public Region<K, V> getDataSet()
	{
		return dataSet;
	}
	
	/**
	 * @param functionId the functionId to set
	 */
	public void setFunctionId(String functionId)
	{
		this.functionId = functionId;
	}

	/**
	 * @return the cache
	 */
	@Override
	public Cache getCache()
	{
		return cache;
	}

	private String functionId;
	private Region<K, V> dataSet;
	private Object arguments;
	private Set<?> filter;
	private ResultSender<T> resultSender;
	private final Cache cache;

}

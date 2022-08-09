package com.vmware.data.services.gemfire.io;

import java.util.Collection;

import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.FunctionDomainException;
import org.apache.geode.cache.query.NameResolutionException;
import org.apache.geode.cache.query.Query;
import org.apache.geode.cache.query.QueryInvocationTargetException;
import org.apache.geode.cache.query.TypeMismatchException;

import nyla.solutions.core.exception.SystemException;

public class QuerierMgr implements QuerierService
{

	@Override
	public <ReturnType> Collection<ReturnType> query(Query query, RegionFunctionContext rfc, Object... params)
	{
		try
		{
			return Querier.query(query, rfc, params);
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch (TypeMismatchException|NameResolutionException| FunctionDomainException | QueryInvocationTargetException e)
		{
			throw new SystemException(e);
		}
	}//------------------------------------------------

	@Override
	public <ReturnType> Collection<ReturnType> query(String query, Object... params)
	{
		return Querier.query(query, params);
		
	}

	@Override
	public <ReturnType> Collection<ReturnType> query(String query, RegionFunctionContext rfc, Object... params)
	{
		return Querier.query(query, rfc,params);
	}

}

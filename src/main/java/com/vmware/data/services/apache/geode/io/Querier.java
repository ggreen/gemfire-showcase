package com.vmware.data.services.apache.geode.io;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.FunctionDomainException;
import org.apache.geode.cache.query.NameResolutionException;
import org.apache.geode.cache.query.Query;
import org.apache.geode.cache.query.QueryInvocationTargetException;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.SelectResults;
import org.apache.geode.cache.query.TypeMismatchException;

import com.vmware.data.services.apache.geode.functions.JvmRegionFunctionContext;

public class Querier
{
	/**
	 * 
	 * @param query the OQL query
	 * @param <ReturnType> the return type
	 * @param params the bind variables
	 * @return the collection of the return types
	 */
	public static <ReturnType> Collection<ReturnType> query(String query,Object...params)
	{
		return query(query, (RegionFunctionContext)null,params);
	}// ------------------------------------------------
	/**
	 * 
	 * @param <ReturnType> the collection of return types
	 * @param query the OQL
	 * @param rfc the region function context
	 * @param params the bind variables
	 * @return Collection of the return type
	 */
	public static <ReturnType> Collection<ReturnType> query(String query, RegionFunctionContext rfc, Object... params)
	{
		try
		{

			QueryService queryService = CacheFactory.getAnyInstance().getQueryService();

			// Create the Query Object.
			Query queryObj = queryService.newQuery(query);

			return query(queryObj, rfc,params);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Cannot execute query:" + query + " ERROR:" + e.getMessage(), e);
		}

	}// -------------------------------------------------------------------
	/**
	 * Select results for OQL
	 * @param <ReturnType> the collection return type
	 * @param queryObj the query object
	 * @param rfc the region function context
	 * @param params the bind variables
	 * @return collection of results
	 * @throws FunctionDomainException function error occurs
	 * @throws TypeMismatchException type error occurs
	 * @throws NameResolutionException name resolution exception occurs
	 * @throws QueryInvocationTargetException when query invocation target exception occurs
	 */
	@SuppressWarnings("unchecked")
	public static <ReturnType> Collection<ReturnType> query(Query queryObj, RegionFunctionContext rfc, Object... params)
	throws FunctionDomainException, TypeMismatchException, NameResolutionException, QueryInvocationTargetException
	{
		SelectResults<ReturnType> selectResults;

		// Execute Query locally. Returns results set.

		if (rfc == null || JvmRegionFunctionContext.class.isAssignableFrom(rfc.getClass()))
		{
			if(params == null || params.length == 0)
			{
				selectResults = (SelectResults<ReturnType>) queryObj.execute();				
			}
			else
			{
				selectResults = (SelectResults<ReturnType>) queryObj.execute(params);
			}
			if (selectResults == null || selectResults.isEmpty())
				return null;

			ArrayList<ReturnType> results = new ArrayList<ReturnType>(selectResults.size());
			results.addAll(selectResults.asList());

			return results;
		}
		else
		{
			if(params == null || params.length == 0)
			{
				selectResults = (SelectResults<ReturnType>) queryObj.execute(rfc);	
			}
			else
			{
				selectResults = (SelectResults<ReturnType>) queryObj.execute(rfc,params);
			}
			

			if (selectResults == null || selectResults.isEmpty())
				return null;

			return selectResults;
		}

	}

}

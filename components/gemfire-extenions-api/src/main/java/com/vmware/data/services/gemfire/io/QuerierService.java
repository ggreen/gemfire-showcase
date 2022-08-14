package com.vmware.data.services.gemfire.io;

import java.util.Collection;

import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.Query;

/**
 * Interface for a query support object
 * 
 * @author Gregory Green
 *
 */
public interface QuerierService
{
	<ReturnType> Collection<ReturnType> query(Query query, RegionFunctionContext rfc, Object... params);
	
	<ReturnType> Collection<ReturnType> query(String query, Object... params);
	<ReturnType> Collection<ReturnType> query(String query, RegionFunctionContext rfc, Object... params);
}

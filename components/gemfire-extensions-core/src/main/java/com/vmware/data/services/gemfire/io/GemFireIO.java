package com.vmware.data.services.gemfire.io;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.cache.execute.ResultSender;

import nyla.solutions.core.exception.DataException;


/**
 * Utility class for the processing results
 * @author Gregory Green
 *
 */
public final class GemFireIO
{
	private GemFireIO()
	{
		
	}// --------------------------------------------
	/**
	 * Determine if the data should be sent
	 * @param resultSender the result sender implementation
	 * @param data the data
	 * @return true if data is an exception
	 */
	public static boolean isErrorAndSendException(ResultSender<Object> resultSender, Object data)
	{
		if(data instanceof Throwable)
		{
			Throwable e = (Throwable)data;			
			
			resultSender.sendException(e);
			return true;
		}
		
		return false;
	}// --------------------------------------------
	/**
	 * Execute a function with the given execution settings
	 * @param execution the function service execution settings
	 * @param function the function to be execute
	 * @param <T> the type
	 * @return the flatten results from one or more servers
	 * @throws Exception when remote execution errors occur
	 */
	@SuppressWarnings("unchecked")
	public  static <T> Collection<T> exeWithResults(Execution<?,?,?> execution, Function<?> function)
			throws Exception
	{
		ResultCollector<?, ?> resultCollector;
		try 
		{
			resultCollector = execution.execute(function);
		}
		catch (FunctionException e) 
		{
			if(e.getCause() instanceof NullPointerException)
				throw new RuntimeException("Unable to execute function:"+function.getId()+
						" assert hostnames(s) for locators and cache server can be resovled. "+
						" If you do not have access to the host file, create host.properties and add to the CLASSPATH. "+
						" Example: locahost=127.1.0.0 "+
						" also assert that all cache servers have been initialized. Check if the server's cache.xml has all required <initializer>..</initializer> configurations",
						e);
			else
				throw e;
		}
		
		Object resultsObject = resultCollector.getResult();
		
		//Return a result in collection (for a single response)
		Collection<Object> collectionResults = (Collection<Object>)resultsObject;
			
		//if empty return null
		if(collectionResults == null ||  collectionResults.isEmpty())
			return null;
		
		Collection<Object> list = new ArrayList<Object>(collectionResults.size());
		
		flatten(collectionResults, list);

		if(list.isEmpty())
			return null;
					
		return (Collection<T>)list;		
		
	}// --------------------------------------------------------
	/**
	 * Execute a function with the given execution settings
	 * @param execution the function service execution settings
	 * @param functionId the function ID to be executed
	 * @param <T> the type
	 * @return the flatten results from one or more servers
	 * @throws Exception when remote execution errors occur
	 */
	@SuppressWarnings("unchecked")
	public  static <T> Collection<T> exeWithResults(Execution<?,?,?> execution, String functionId)
			throws Exception
	{
		ResultCollector<?, ?> resultCollector;
		try 
		{
			resultCollector = execution.execute(functionId);
		}
		catch (FunctionException e) 
		{
			if(e.getCause() instanceof NullPointerException)
				throw new RuntimeException("Unable to execute function:"+functionId+
						" assert hostnames(s) for locators and cache server can be resovled. "+
						" If you do not have access to the host file, create host.properties and add to the CLASSPATH. "+
						" Example: locahost=127.1.0.0 "+
						" also assert that all cache servers have been initialized. Check if the server's cache.xml has all required <initializer>..</initializer> configurations",
						e);
			else
				throw e;
		}
		
		Object resultsObject = resultCollector.getResult();
		
		//Return a result in collection (for a single response)
		Collection<Object> collectionResults = (Collection<Object>)resultsObject;
			
		//if empty return null
		if(collectionResults.isEmpty())
			return null;
		
		Collection<Object> list = new ArrayList<Object>(collectionResults.size());
		
		flatten(collectionResults, list);

		if(list.isEmpty())
			return null;
					
		return (Collection<T>)list;		
		
	}// --------------------------------------------------------	
	/**
	 * 
	 * @param region the region
	 * @param  <T> the type
	 * @return the set of keys
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Collection<T> keySetOnServer(Region<?,?> region)
	{
		try
		{
			if(DataPolicy.EMPTY.equals(region.getAttributes().getDataPolicy()))
					return (Collection)region.keySetOnServer();
			else
				return (Collection)region.query("select * from /"+region.getName()+".keySet()");
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DataException("region:"+region,e);
		}
		
	}//--------------------------------------------------------
	/**
	 * Used to flatten results from multiple servers
	 * @param input the unflatten input
	 * @param flattenOutput the flatten results
	 * @param <T> the return type of collection objects
	 * @throws Exception if an any input collection items are exceptions
	 */
	@SuppressWarnings("unchecked")
	public static <T> void flatten(Collection<Object> input,
			Collection<Object> flattenOutput)
	throws Exception
	{
		if (input == null || input.isEmpty() || flattenOutput == null)
			return;

		for (Object inputObj : input)
		{
			if(inputObj instanceof Exception )
				throw (Exception)inputObj;
			
			if(inputObj == null)
				continue;
			
			if(inputObj instanceof Collection)
				flatten((Collection<Object>)inputObj,flattenOutput);
			else
				flattenOutput.add(inputObj);

		}

	}// --------------------------------------------------------	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Collection<T> collectResults(ResultCollector<?, ?> resultCollector)
	throws Exception
	{
		if(resultCollector == null)
			return null;
		
		Collection<Object> results = (Collection)resultCollector.getResult();
		
		if(results  == null || results.isEmpty())
			return null;
			
		ArrayList<Object> output = new ArrayList<>(10);
		flatten(results, output);
		
		if(output.isEmpty())
			return null;
		
		output.trimToSize();
		return (Collection)output;
	}
}

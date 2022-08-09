package com.vmware.data.services.gemfire.operations.functions;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.logging.log4j.LogManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class PrintExampleJsonFunction implements Function<Object>
{
	public void execute(FunctionContext<Object> fc)
	{
		ResultSender<String> rs = fc.getResultSender();
		
		String[] args = (String[]) fc.getArguments();
		try
		{
			
			if(args == null || args.length == 0)
				throw new FunctionException("args");
			
			String className = args[0];
			
			if(className == null || className.length() == 0)
			{
				rs.lastResult("{}");
				return;
			}
		
		
	
			ObjectMapper objectMapper = new ObjectMapper();
			
			Object obj = forClassName(className).newInstance();
			
			rs.lastResult(objectMapper.writeValueAsString(obj));
		}
	
		catch (ClassNotFoundException| IllegalAccessException | InstantiationException | JsonProcessingException| RuntimeException e)
		{
			LogManager.getLogger(getClass()).error(e.toString());
			rs.sendException(e);
		}
	}// --------------------------------------------------------
	private Class<?> forClassName(String className) 
	throws ClassNotFoundException
	{
		if(className == null || className.length() == 0)
			throw new FunctionException("class name string is empty: "); 
		
		
		return Class.forName(className);
	}// --------------------------------------------------------

	public String getId()
	{
		return "PrintExampleJsonFunction";
	}

	public boolean hasResult()
	{
		return true;
	}

	public boolean isHA()
	{
		return false;
	}

	public boolean optimizeForWrite()
	{
		return true;
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 8579019703991148580L;

}

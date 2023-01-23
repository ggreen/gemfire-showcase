package io.spring.gemfire.rest.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.geode.cache.RegionDestroyedException;
import org.apache.geode.cache.client.ServerOperationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import nyla.solutions.core.util.Debugger;

@Component
public class FaultAgent
{

	@ExceptionHandler
	 public DataError handleException(HttpServletRequest request, HttpServletResponse response, Exception e )
	 {

	    	var dataError = new DataError();
	    	
	    	dataError.setError("Processing error");
	    	if(e != null)
	    	{
	    		var cause = e.getCause();

	    		dataError.setMessage(e.getMessage());
	    		
	            if(e instanceof ServerOperationException)
	            {
	                dataError.setError("Server operation error");

	                if(cause instanceof RegionDestroyedException)
	                {
	                	RegionDestroyedException regionDestroyedException = (RegionDestroyedException)cause;
	                    dataError.setMessage("Region region:"+regionDestroyedException.getRegionFullPath()+" not found");
	                }
	            }
	            
	    	}
	    	
	        dataError.setPath(request.getRequestURI());
	        dataError.setStackTrace(Debugger.stackTrace(e));
	        response.setStatus(500);
	        
	        

	        return dataError;
	    }
}

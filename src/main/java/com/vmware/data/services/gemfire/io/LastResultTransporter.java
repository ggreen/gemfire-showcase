package com.vmware.data.services.gemfire.io;

import java.io.Serializable;

import org.apache.geode.cache.execute.ResultSender;

/**
 * Implementation of the Result Transporter to send the last results
 * @author Gregory Green
 */
public class LastResultTransporter implements ResultTransporter
{
	/**
	 * Calls the ResultSender's lastResult method
	 * @param resultSender Gemfire result sender
	 * @param data the data used to send
	 */
	//@Override
	public void send(ResultSender<Object> resultSender, Object data)
	{
		
		if(!GemFireIO.isErrorAndSendException(resultSender,data))
		{
			resultSender.lastResult((Serializable)data);	
		}		
	}// --------------------------------------------
	

}

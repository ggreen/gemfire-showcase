package io.spring.gemfire.rest.app.exception;

public class DataServiceSystemException extends RuntimeException
{

	public DataServiceSystemException()
	{
		super();
	}

	public DataServiceSystemException(String message, Throwable cause, boolean enableSuppression,
	boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataServiceSystemException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DataServiceSystemException(String message)
	{
		super(message);
	}

	public DataServiceSystemException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4069020849121606422L;

}

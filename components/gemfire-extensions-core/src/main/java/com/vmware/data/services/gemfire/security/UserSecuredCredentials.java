package com.vmware.data.services.gemfire.security;

import nyla.solutions.core.security.SecuredToken;

/**
 * @author Gregory Green
 *
 */
public class UserSecuredCredentials implements SecuredToken
{
	private static final long serialVersionUID = -8233621362316652259L;
	
	/**
	 * 
	 * @param name the username
	 * @param credentials the user credentials
	 * @param token the security token
	 */
	public UserSecuredCredentials(String name, char[] credentials, String token)
	{
		super();
		this.name = name;
		this.credentials = credentials;
		this.token = token;
	}
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * @return the token
	 */
	public String getToken()
	{
		return token;
	}
	/**
	 * @return the credentials
	 */
	public char[] getCredentials()
	{
		return credentials;
	}

	private final String name;
	private final String token;
	private final char[] credentials;

}

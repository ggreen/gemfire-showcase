package com.vmware.data.services.apache.geode.security;

import org.apache.geode.security.AuthInitialize;

/**
 * SecurityFactory is representable for the create authorization/authentication objects
 * @author Gregory Green
 *
 */
public class SecurityFactory
{
	private SecurityFactory()
	{
	}
	/**
	 * 
	 * @return new CryptionPropertyAuthInitialize()
	 */
	public static AuthInitialize createAuthInitialize()
	{
		//DO NOT USE spring
		return new CryptionPropertyAuthInitialize();
	}// ------------------------------------------------
}

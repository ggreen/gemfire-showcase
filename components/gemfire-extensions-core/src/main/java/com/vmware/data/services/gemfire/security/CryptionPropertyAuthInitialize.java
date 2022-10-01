package com.vmware.data.services.gemfire.security;

import java.util.Properties;

import org.apache.geode.LogWriter;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.security.AuthInitialize;
import org.apache.geode.security.AuthenticationFailedException;

import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.settings.Settings;

/**
 * This object will main user/password based on a property file of encrypted password
 * @author Gregory Green
 *
 */
@SuppressWarnings("deprecation")
public class CryptionPropertyAuthInitialize implements AuthInitialize
//TODO:, SecurityConstants
{
	/**
	 * FunctionAttribute password cryption prefix
	 * CRYPTION_PREFIX = "{cryption}"
	 */
	public static final String CRYPTION_PREFIX = "{cryption}";
	
	//@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}// ------------------------------------------------
	/**
	 * 
	 * @return an new instance CryptionPropertyAuthInitialize
	 */
	public static AuthInitialize create() 
	{
		    return new CryptionPropertyAuthInitialize();
	}// ------------------------------------------------
	/**
	 *  Initialize with the given set of security properties and return the credentials for the peer/client as properties.
	 *  @param properties the security properties (see SecurityConstants PROP values)
	 *  @param distributedMember the distributed member
	 *  @param isPeer boolean if the distribute member is a peer
	 *  @return properties with keys SecurityConstants.USERNAME_PROP and SecurityConstants.PASSWORD_PROP 
	 *  @throws AuthenticationFailedException the username property not provided
	 */
	//@Override
	public Properties getCredentials(Properties properties, DistributedMember distributedMember,
			boolean isPeer) throws AuthenticationFailedException
	{
		if(properties == null || properties.isEmpty())
			    	throw new AuthenticationFailedException("properties are null or empty");
		//get user
		String username = properties.getProperty(SecurityConstants.USERNAME_PROP);
		if(username == null || username.length() == 0)
			throw new AuthenticationFailedException(" FunctionAttribute \""+SecurityConstants.USERNAME_PROP+"\" not provided");
		
		
		try
		{
			//Get encrypted
			String passwordKey = username+".password";
			String encryptedPassword = null;

			encryptedPassword = settings.getProperty(passwordKey);

			if(encryptedPassword == null || encryptedPassword.length() == 0)
				throw new AuthenticationFailedException("FunctionAttribute \""+passwordKey+" for found in config.properties ");
			
			encryptedPassword = encryptedPassword.trim();
			
			/*if(!encryptedPassword.startsWith(CRYPTION_PREFIX))
			{
				throw new AuthenticationFailedException("Password not encrypted or not prefixed with \""+CRYPTION_PREFIX+"\"");
			}*/
						
			//set password in output properties
			Properties output = new Properties();
			output.setProperty(SecurityConstants.USERNAME_PROP, username);
			output.setProperty(SecurityConstants.PASSWORD_PROP, encryptedPassword);
			
			return output;
		}
		catch (SecurityException e)
		{
			throw new AuthenticationFailedException(e.getMessage());
		}
		
	}// ------------------------------------------------	

	//@Override
	public void init(LogWriter systemLogger, LogWriter securityLogger)
			throws AuthenticationFailedException
	{
	}// ------------------------------------------------
	private static Settings settings = Config.getSettings();
	//private ResourceBundle config = ResourceBundle.getBundle("config");
}

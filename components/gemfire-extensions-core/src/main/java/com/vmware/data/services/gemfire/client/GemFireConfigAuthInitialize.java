package com.vmware.data.services.gemfire.client;

import java.util.Properties;

import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.security.AuthInitialize;
import org.apache.geode.security.AuthenticationFailedException;

import nyla.solutions.core.security.SecuredToken;
import nyla.solutions.core.util.Config;

/**
 * <pre>
 * Set ENVIRONMENT
 * 
 * security-username or SECURITY_USERNAME
 * security-password or SECURITY_PASSWORD 
 * 
 * USER_NAME_PROP);
	    String token = Config.getProperty(TOKEN,"")
	    
   </pre>
 * @author Gregory Green
 *
 */
public class GemFireConfigAuthInitialize
implements AuthInitialize, GemFireConfigConstants
{
	public static final String SECURITY_PASSWORD_PROP = "SECURITY_PASSWORD";
	public static final String SECURITY_USER_PROP = "SECURITY_USERNAME";

	private final GemFireSettings vcapConfig;

	/**
	   * Constructor
	   * @param vcapConfig the VCAP configuration
	   */
	  protected GemFireConfigAuthInitialize(GemFireSettings vcapConfig)
	  {
		  this.vcapConfig = vcapConfig;
	  }

	  public static AuthInitialize create() {
	    return new GemFireConfigAuthInitialize(GemFireSettings.getInstance());
	  }

	  @Override
	  public void close() {
	  }

	  @Override
	  public Properties getCredentials(Properties inputProperties, DistributedMember distributedMember,
	                                   boolean arg2) throws AuthenticationFailedException 
	  {

	    Properties props = new Properties();
	    String username = getSecurityUserName();
	    String password = getSecurityPassword();
	    String token = Config.getProperty(TOKEN,"");
	    
	    
	     SecuredToken securedToken = vcapConfig.getSecuredToken(username, token);
	     	     
	     if(securedToken == null)
	     {
	    	 props.setProperty(USER_NAME, username);
	    	 props.setProperty(PASSWORD, password);
	    	 return props;
	     }
	     
	     System.out.println("ISSUE-SECURED_TOKEN _NAME"+securedToken.getName());
	     
	      password = String.valueOf(securedToken.getCredentials());
	      props.setProperty(USER_NAME, securedToken.getName());
	      props.setProperty(PASSWORD, password);
	      props.setProperty(TOKEN, token);
	      
	    return props;
	  }//------------------------------------------------
	protected String getSecurityPassword()
	{

		String password = Config.getProperty(PASSWORD,Config.getProperty(SECURITY_PASSWORD_PROP,""));
		return password;
	}//------------------------------------------------
	protected String getSecurityUserName()
	{

		String username = Config.getProperty(USER_NAME,Config.getProperty(SECURITY_USER_PROP,""));
		return username;
	}//------------------------------------------------

}

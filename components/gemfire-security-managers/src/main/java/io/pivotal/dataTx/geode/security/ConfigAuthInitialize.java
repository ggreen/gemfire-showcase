package io.pivotal.dataTx.geode.security;

import java.util.Properties;

import nyla.solutions.core.util.settings.Settings;
import org.apache.geode.LogWriter;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.security.AuthInitialize;
import org.apache.geode.security.AuthenticationFailedException;

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
@SuppressWarnings("deprecation")
public class ConfigAuthInitialize
implements AuthInitialize
{
	public final static String USER_NAME = "security-username";
	public final static String PASSWORD = "security-password";
	public final static String TOKEN = "security-token";
	private final Settings config = Config.settings();
	
	  /**
	   * Constructor
	   */
	  protected ConfigAuthInitialize()
	  {
	  }

	  public static AuthInitialize create() {
	    return new ConfigAuthInitialize();
	  }

	  @Override
	  public void close() {
	  }

	  @Override
	  public Properties getCredentials(Properties inputProperties, DistributedMember distributedMember,
	                                   boolean arg2) throws AuthenticationFailedException 
	  {

	    Properties props = new Properties();
	    String username = inputProperties.getProperty(SecurityConstants.USERNAME_PROP);;
	    
	    if(username == null || username.length() == 0 )
	    	username = getSecurityUserName();
	    
	    String password = inputProperties.getProperty(SecurityConstants.PASSWORD_PROP);;
	    
	    if(password == null || password.length() == 0 )
	    	password = getSecurityPassword();
	    
	    String token = config.getProperty(TOKEN,"");
	    
	        props.setProperty(USER_NAME, username);
	      props.setProperty(PASSWORD, password);
	      props.setProperty(TOKEN, token);
	      
	    return props;
	  }

	protected String getSecurityPassword()
	{
		String password = config.getProperty(PASSWORD,config.getProperty("SECURITY_PASSWORD",""));
		return password;
	}

	protected String getSecurityUserName()
	{
		String username = config.getProperty(USER_NAME,config.getProperty("SECURITY_USERNAME",""));
		return username;
	}

	@Override
	  public void init(LogWriter logWriter, LogWriter securityLogWriter)
	      throws AuthenticationFailedException 
	  {
	  }
}

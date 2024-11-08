package io.pivotal.dataTx.geode.security;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Properties;

import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.ResourcePermission;
import org.apache.geode.security.SecurityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nyla.solutions.core.util.Cryption;
import nyla.solutions.core.util.Debugger;


/**
 * User based security manager
 * @author Gregory Green
 *
 */
public class UserSecurityManager implements SecurityManager
{
	
    private Logger logger = null;
    private final UserService userService;
	
	public UserSecurityManager()
	{
		this(new SettingsUserService());
	}

	
	public UserSecurityManager(UserService userService)
	{		
		this.userService = userService;
	}

	@Override
	public Object authenticate(Properties credentials) throws AuthenticationFailedException
	{
		if(credentials == null )
			throw new AuthenticationFailedException("null properties, properties required");
			
		String userName  = null;
		
		userName = credentials.getProperty(SecurityConstants.USERNAME_PROP);	
		if (userName == null || userName.length() == 0){
				throw new AuthenticationFailedException(SecurityConstants.USERNAME_PROP+" required");
		}
		
		String password = credentials.getProperty(SecurityConstants.PASSWORD_PROP);
			
		if (password == null)
				throw new AuthenticationFailedException(SecurityConstants.PASSWORD_PROP+" required");
			
			password = password.trim();
			
			try
			{
				password = Cryption.removePrefix(password);
				
				User user = this.userService.findUser(userName);
				
				if(user == null)
					throw new AuthenticationFailedException("user \""+userName+"\" not found");
		
				
				byte[] userEncryptedPasswordBytes = user.getEncryptedPassword();
				
				if(userEncryptedPasswordBytes == null || userEncryptedPasswordBytes.length == 0)
					throw new AuthenticationFailedException("password is required");
				
				Cryption cryption = new Cryption();
				String userEncryptedPassword =  new String(userEncryptedPasswordBytes,StandardCharsets.UTF_8);
			
				
				String storedUnEncrypted = null;
				
				//test encrypted
				if(userEncryptedPassword.equals(password))
					return user;
				
				
				//test without encrypt
				storedUnEncrypted = cryption.decryptText(userEncryptedPassword);
				
				if(storedUnEncrypted.equals(password))
					return user;
				
			
				try
				{
					String unencryptedPassword = cryption.decryptText(password);
					
					if(unencryptedPassword.equals(storedUnEncrypted))
							return user;
				}
				catch(IllegalArgumentException e)
				{
					throw new AuthenticationFailedException("Security password or user not found.");
				}
				
				throw new AuthenticationFailedException("Security user or password not found");
			}
			catch(SecurityException e)
			{
				this.getLogger().warn("SECURITY EXCEPTION user:"+userName+" ERROR:"+Debugger.stackTrace(e));
				throw new AuthenticationFailedException(e.getMessage(),e);
			}
			catch(AuthenticationFailedException e)
			{
				this.getLogger().warn(" AuthenticationFailedException user:"+userName+" ERROR:"+e.getMessage());
				throw e;
			}
			catch (Exception e)
			{
				this.getLogger().error("Exception:"+Debugger.stackTrace(e));
				throw new AuthenticationFailedException(e.getMessage()+" ERROR:"+Debugger.stackTrace(e));
			}
			
			
	}

    public boolean authorize(Object principal, ResourcePermission permission)
    {
    	if(principal == null)
    		return false;
    	
    	if(!User.class.isAssignableFrom(principal.getClass()))
    		return false;
    	User user = (User)principal;
    	

    	
    	//this MUST BE FAST!!!
    	
    	Collection<String> privileges = user.getPriviledges();
    	
    	if(privileges == null || privileges.isEmpty())
    		return false;
    	
    	String textPermission = permission.toString();
    	
    	boolean hasPermission =  privileges.parallelStream().anyMatch(p -> p.equals("ALL") || textPermission.contains(p));
    	
    	if(!hasPermission)
    	{
        	getLogger().warn("user:"+user.getUserName()+" DOES NOT HAVE permission:"+textPermission);
    	}
    	else
		{
			getLogger().info("user:"+user.getUserName()+" HAS permission:"+textPermission);
		}
    	
    	return hasPermission;
    }

    private Logger getLogger()
    {
    	if(logger == null)
    		logger = LogManager.getLogger(UserSecurityManager.class);
    	
    	return logger;
    }

    protected void setLogger(Logger logger)
    {
    	this.logger = logger;
    }



}

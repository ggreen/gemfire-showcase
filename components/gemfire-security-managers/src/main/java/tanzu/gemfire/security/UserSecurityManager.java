package tanzu.gemfire.security;

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
import tanzu.gemfire.security.settings.SettingsUserService;


/**
 * User based security manager
 * @author Gregory Green
 *
 */
public class UserSecurityManager implements SecurityManager
{
	
    private Logger logger = null;
    private final UserService userService;
	private final Cryption userSecurityManagerCryption;

	public UserSecurityManager()
	{
		this(new SettingsUserService());
	}

	
	public UserSecurityManager(UserService userService)
	{
		this.userSecurityManagerCryption = new Cryption();
		this.userService = userService;
	}

	@Override
	public Object authenticate(Properties credentials) throws AuthenticationFailedException
	{
		if(credentials == null || credentials.isEmpty())
			throw new AuthenticationFailedException("null or empties properties, properties required");
			
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
				User user = this.userService.findUser(userName);

				if(user == null)
					throw new AuthenticationFailedException("user \""+userName+"\" not found");


				byte[] userEncryptedPasswordBytes = user.getEncryptedPassword();
				if(userEncryptedPasswordBytes == null || userEncryptedPasswordBytes.length == 0)
					throw new AuthenticationFailedException("password is required");

				String userEncryptedPassword =  new String(userEncryptedPasswordBytes,StandardCharsets.UTF_8);

                String decryptedUserPassword = null;
                try {
                    decryptedUserPassword = userSecurityManagerCryption.decryptText(userEncryptedPassword);
                } catch (Exception e) {
                    throw new AuthenticationFailedException("Check saved user properties password encrypted with proper encryption key");
                }

                //not encrypted
				String decryptedPassword;
				try {
					decryptedPassword = Cryption.interpret(password);
				} catch (Exception e) {
					throw new AuthenticationFailedException("Check provided password encrypted with proper encryption key");
				}


				if(decryptedUserPassword.equals(decryptedPassword))
					return user;


				try {
					decryptedPassword = userSecurityManagerCryption.decryptText(password);
				} catch (Exception e) {
					throw new AuthenticationFailedException("Security password or user not found");
				}

				if(decryptedUserPassword.equals(decryptedPassword))
					return user;

				throw new AuthenticationFailedException("Security password or user does not match or exist");
			}
			catch(SecurityException e)
			{
                this.getLogger().warn("SECURITY EXCEPTION user:{} ERROR:{}", userName, Debugger.stackTrace(e));
				throw new AuthenticationFailedException(e.getMessage(),e);
			}
			catch(AuthenticationFailedException e)
			{
                this.getLogger().warn(" AuthenticationFailedException user:{} ERROR:{}", userName, e.getMessage());
				throw e;
			}
			catch (Exception e)
			{
                this.getLogger().error("Exception:{}", Debugger.stackTrace(e));
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
            getLogger().warn("user:{} DOES NOT HAVE permission:{}", user.getUserName(), textPermission);
    	}
    	else
		{
            getLogger().info("user:{} HAS permission:{}", user.getUserName(), textPermission);
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

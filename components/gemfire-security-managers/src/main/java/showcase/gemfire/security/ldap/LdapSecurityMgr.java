package showcase.gemfire.security.ldap;

import java.security.Principal;
import java.util.Properties;

import javax.naming.NamingException;

import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.NotAuthorizedException;
import org.apache.geode.security.ResourcePermission;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import showcase.gemfire.security.AclSecurityPropertiesDirector;
import showcase.gemfire.security.SecurityConstants;
import showcase.gemfire.security.exceptions.MissingSecurityProperty;
import nyla.solutions.core.data.expiration.ExpiringKeyValueLookup;
import nyla.solutions.core.ds.LDAP;
import nyla.solutions.core.exception.ConfigException;
import nyla.solutions.core.security.data.SecurityAcl;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Cryption;
import nyla.solutions.core.util.Debugger;

/**
 * <pre>
 * Security Manager for LDAP based authentication and authorization.
 * 
 * This security manager supports
 * 
 * - Password Encryption
 * - Caching credential with an expiration
 * 
 * </pre>
 * @author Gregory Green
 *
 */
public class LdapSecurityMgr implements org.apache.geode.security.SecurityManager
{

	/**
	 * CRYPTION_KEY_PROP = Cryption.getCryptionKey()
	 */
	public static String CRYPTION_KEY = Cryption.getCryptionKey();
	
	private String proxyPassword = null;	
	private SecurityAcl acl = null;
	private String basedn = null;
	private String uidAttribute = null;
	private String memberOfAttrNm = "memberOf"; 
	private String groupAttrNm  = null; //Ex: "CN";
	protected Logger securityLogger;
	private String serviceAccountDn = null;
	private int timeout = Config.settings().getPropertyInteger("LDAP_TIMEOUT", 10).intValue();
	private LDAPConnectionFactory ldapConnectionFactory = new LDAPConnectionFactory();
	private String ldapUrl;
	private ExpiringKeyValueLookup<String, Principal> cachedCredentials =null;
	
	public static org.apache.geode.security.SecurityManager create()
	{
		return new LdapSecurityMgr();
	}


	public LdapSecurityMgr()
	{
		this.securityLogger = null;

	}


	/**
	 * @param principal the principal to authorize
	 * @param context the permission to authorize
	 */
	@Override
	public boolean authorize(Object principal, ResourcePermission context)
	{
		try
		{
			if (principal == null)
			{
				securityLogger.warn("Not authorized SecurityManager principal is null for context" + context);
				return false;
			}

			if (context == null)
			{
				securityLogger.warn("Not authorized SecurityManager context is null for principal" + principal);
				return false;
			}

			var startTime = System.currentTimeMillis();

			boolean authorized = acl.checkPermission((Principal) principal, toString(context));

            securityLogger
					.debug("AUTH: {}, in {} milliseconds for principal:{} context:{} authorized:{} acl:{}",
							authorized, System.currentTimeMillis()-startTime, principal, context, authorized, acl);

			return authorized;

		}
		catch (AuthenticationFailedException e)
		{
			this.securityLogger.warn(e);
			
			throw e;
		}
		catch (RuntimeException e)
		{

			this.securityLogger.warn(e);

			throw new AuthenticationFailedException(e.getMessage() + " STACK:" + Debugger.stackTrace(e));

		}

	}

	@Override
	public void init(final Properties securityProps)
	throws NotAuthorizedException
	{
		setup(securityProps);
	}

	String toString(ResourcePermission resourcePermission)
	{
		return String.valueOf(resourcePermission);

	}

	/**
	 * Set up the security manager
	 * @param securityProps the security properties
	 * @throws MissingSecurityProperty when a required property does not exist
	 * 
	 */
	protected void setup(Properties securityProps)
	throws MissingSecurityProperty
	{
		
		setupLocalCredentialCaching(securityProps);
		
		this.serviceAccountDn = Config.config().getPropertyEnv(LdapSecurityConstants.LDAP_PROXY_DN,securityProps);

		securityLogger.debug(LdapSecurityConstants.LDAP_PROXY_DN + " *************" + serviceAccountDn);

		if (serviceAccountDn == null || serviceAccountDn.length() == 0)
		{
			throw new MissingSecurityProperty(LdapSecurityConstants.LDAP_PROXY_DN);
		}

		setupProxyPassword(securityProps);

		this.ldapUrl = Config.config().getPropertyEnv(LdapSecurityConstants.LDAP_SERVER_URL_PROP,securityProps);
		if (this.ldapUrl == null || this.ldapUrl.length() == 0)
			throw new MissingSecurityProperty(LdapSecurityConstants.LDAP_SERVER_URL_PROP);
			
			
		this.basedn = Config.config().getPropertyEnv(LdapSecurityConstants.LDAP_BASEDN_NAME_PROP,securityProps);
		if (this.basedn == null || this.basedn.length() == 0)
			throw new MissingSecurityProperty(LdapSecurityConstants.LDAP_BASEDN_NAME_PROP);
		
		this.memberOfAttrNm = Config.config().getPropertyEnv(LdapSecurityConstants.LDAP_MEMBEROF_ATTRIB_NM_PROP,securityProps);
		if (this.memberOfAttrNm == null || this.memberOfAttrNm.length() == 0)
			throw new MissingSecurityProperty(LdapSecurityConstants.LDAP_MEMBEROF_ATTRIB_NM_PROP);
		
		
		this.groupAttrNm = Config.config().getPropertyEnv(LdapSecurityConstants.LDAP_GROUP_ATTRIB_NM_PROP,securityProps);
		if (this.groupAttrNm == null || this.groupAttrNm.length() == 0)
			throw new MissingSecurityProperty(LdapSecurityConstants.LDAP_GROUP_ATTRIB_NM_PROP);
		
		this.uidAttribute =Config.config().getPropertyEnv(LdapSecurityConstants.LDAP_UID_ATTRIBUTE,securityProps);
		if (this.uidAttribute == null || this.uidAttribute.length() == 0)
		{
			this.uidAttribute = "uid";
		}

		// check to LDAP settings
		try (LDAP ldap = this.ldapConnectionFactory.connect(ldapUrl, serviceAccountDn, proxyPassword.toCharArray()))
		{
		}
		catch (NamingException e)
		{
			securityLogger.warn(e);
			throw new AuthenticationFailedException(e.getMessage(), e);
		}

		AclSecurityPropertiesDirector director =  new AclSecurityPropertiesDirector(securityProps,
		LdapSecurityConstants.LDAP_ACL_GROUP_PREFIX,
		LdapSecurityConstants.LDAP_ACL_USER_PREFIX);
		
		LdapAclBuilder builder = new LdapAclBuilder();
		director.construct(builder);

		this.acl = builder.getAcl();

	}

	/**
	 * 
	 * @param securityProps the properties containing the password
	 */
	String setupProxyPassword(Properties securityProps)
	{
		
		this.proxyPassword = Config.config().getPropertyEnv(LdapSecurityConstants.LDAP_PROXY_PASSWORD,securityProps);
		if (proxyPassword == null || proxyPassword.length() == 0)
			throw new MissingSecurityProperty(LdapSecurityConstants.LDAP_PROXY_PASSWORD);
		
		this.proxyPassword = Cryption.interpret(proxyPassword);
		
		return this.proxyPassword;
		
	}

	@Override
	public Object authenticate(final Properties props)
	throws AuthenticationFailedException
	{
		if(props == null)
			throw new AuthenticationFailedException(
			"Authentication securities properties not provided");

		var startTime = System.currentTimeMillis();

		//Keeping if local caching is configured
		if(this.cachedCredentials != null)
		{
			Principal cachedPrincipal = this.cachedCredentials.getValue(this.toCredentialKey(props));

			if(cachedPrincipal != null) {
				securityLogger.info("Authenticated user: {} in {} milliseconds",cachedPrincipal.getName(),System.currentTimeMillis()-startTime);
				return cachedPrincipal;
			}
		}

		String userName = props.getProperty(LdapSecurityConstants.USER_NAME_PROP);

		if (userName == null)
		{
			throw new AuthenticationFailedException(
			"property ["+ LdapSecurityConstants.USER_NAME_PROP + "] not provided");
		}

		String passwd = props.getProperty(LdapSecurityConstants.PASSWORD_PROP);

		if (passwd == null || passwd.length() == 0)
		{
			throw new AuthenticationFailedException(
			"property ["+ LdapSecurityConstants.PASSWORD_PROP + "] not provided");
		}

		
		try (LDAP ldap = this.ldapConnectionFactory.connect(this.ldapUrl, this.serviceAccountDn,
		this.proxyPassword.toCharArray()))
		{		
			try
			{
				passwd = Cryption.interpret(passwd);
			}
			catch(Exception e)
			{
				securityLogger.warn("Authentication failed user: {} ,Detected password interpretation error. This may be caused by an incorrect password, but you should check that the CRYPTION_KEY environment variable is a minimum of 16 characters, then regenerate any needed passwords. See https://github.com/nyla-solutions/nyla?tab=readme-ov-file#cryption",
						userName);
				throw new AuthenticationFailedException("Authentication due to decryption error, check the value of check that the encryption environment variables.");
			
			}
		
			if (ldap == null)
				throw new IllegalArgumentException("ldap is required from factory: "+ldapConnectionFactory.getClass().getName());
			
			Principal principal = ldap.authenicate(userName, passwd.toCharArray(), this.basedn, uidAttribute, memberOfAttrNm,
			groupAttrNm, timeout);

            securityLogger.debug("AUTHENTICATED:{}", userName);
			
			//Cached the authenticated user if configured caching
			if(this.cachedCredentials != null)
			{
				this.temporaryCacheCredentails(props, principal);
			}

			securityLogger.info("Authenticated user: {} in {} milliseconds",userName,System.currentTimeMillis()-startTime);

			return principal;
		}
		catch(AuthenticationFailedException e)
		{
            securityLogger.warn("Failed authentication for: {}", userName);
			throw e;
		}
		catch (NamingException |RuntimeException e)
		{
			securityLogger.warn(e);
			throw new AuthenticationFailedException(e.getMessage());
		}

	}

	/**
	 * 
	 * @param ldapConnectionFactory
	 *            the ldapConnectionFactory to set
	 * 
	 */
	void setLdapConnectionFactory(LDAPConnectionFactory ldapConnectionFactory)
	{
		this.ldapConnectionFactory = ldapConnectionFactory;
	}

	/**
	 * Validate if the cache exists
	 * @param props the security credential properties
	 * @return the principal if cached
	 */
	Principal validateCached(Properties props)
	{
		
		return this.cachedCredentials != null? 
			this.cachedCredentials.getValue(toCredentialKey(props)) :
			null;
	}

	/**
	 * 
	 * @param props
	 * @param principal
	 */
	void temporaryCacheCredentails(Properties props, Principal principal)
	{
		this.securityLogger.info("Caching credentials for user:"+principal);
		this.cachedCredentials.putEntry(
			toCredentialKey(props),
			principal);
	}

	/**
	 * Format a unique key based on prop username/password
	 * @param props the properties
	 * @return username-password text
	 */
	String toCredentialKey(Properties props)
	{
		if (props == null || props.isEmpty())
			throw new AuthenticationFailedException("props is required");
		
		return new StringBuilder().append
			(props.getProperty(SecurityConstants.USERNAME_PROP, ""))
			.append("-")
			.append(props.getProperty(SecurityConstants.PASSWORD_PROP, ""))
			.toString();
	}

	/**
	 * Setup Local credentials if expiration period > 0
	 * @param initProps the setting properties
	 */
	void setupLocalCredentialCaching(Properties initProps)
	{
		securityLogger = LogManager.getLogger(getClass());
		
		if(initProps == null || initProps.isEmpty())
			return;
		

		String msTx =  Config.config().getPropertyEnv(SecurityConstants.LDAP_CACHING_EXPIRATION_MS_PROP, initProps,"0");
		
		try
		{
			long milliseconds = Long.valueOf(msTx);
		
			this.cachedCredentials = ExpiringKeyValueLookup.withExpirationMS(milliseconds);
		}
		catch(NumberFormatException e)
		{
			throw new ConfigException("Invalid milliseconds provided for property "+SecurityConstants.LDAP_CACHING_EXPIRATION_MS_PROP+"="+msTx);
		}
		
	}

}

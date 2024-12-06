package tanzu.gemfire.security;

/**
 * Constants for bridge security
 * @author Gregory Green
 *
 */
public interface SecurityConstants
{
	/**
	 * Constant for security
	 * USERNAME_PROP = "security-username"
	 */
	  public static final String USERNAME_PROP = "security-username";

	  /**
	   * Constant for security
	   * PASSWORDE_PROP = "security-password"
	   */
	 public static final String PASSWORD_PROP = "security-password";

	 /**
	  * Constant for LDAP caching credentials
	  */
	public static final String LDAP_CACHING_EXPIRATION_MS_PROP = "security-ldap-caching-expiration-ms";
	
}

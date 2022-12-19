package io.pivotal.dataTx.geode.security.ldap;

import java.security.Principal;

import nyla.solutions.core.ds.security.LdapSecurityUser;

/**
 * LDAP Security constants
 * @author Gregory Green
 *
 */
public interface LdapSecurityConstants
{
	
	/**
	 * LDAP_MEMBEROF_ATTRIB_NM_PROP = "security-ldap-memberOf-attribute" property name
	 * 
	 * Example value is memberOf
	 * 
	 */
	public final static String LDAP_MEMBEROF_ATTRIB_NM_PROP = "security-ldap-memberOf-attribute";
	
	/**
	 * LDAP_GROUP_ATTRIB_NM_PROP = "security-ldap-group-attribute" property name
	 * 
	 * Example value is CN
	 */
	public final static String LDAP_GROUP_ATTRIB_NM_PROP = "security-ldap-group-attribute";
	
	/**
	 * PRoperty that begin with the following security-ldap-acl-group-
	 * are ACL for groups.
	 */
	public final static String LDAP_ACL_GROUP_PREFIX = "security-ldap-acl-group-";
	
	/**
	 * Properties for user ACL permissions
	 * 
	 * LDAP_ACL_USER_PREFIX = "security-ldap-acl-user-"
	 * 
	 */
	public final static String LDAP_ACL_USER_PREFIX = "security-ldap-acl-user-";
	
	/**
	 * USER_NAME_PROP = "security-username"
	 */
	public static final String USER_NAME_PROP = "security-username";
	
	/**
	 * PASSWORD_PROP = "security-password"
	 */
	public static final String PASSWORD_PROP = "security-password";
	
	/**
	 * Default system principal
	 */
	public static final Principal systemPrincipal = new LdapSecurityUser("SYS");
	
	/**
	 * LDAP_SERVER_URL_PROP = "security-ldap-server-url"
	 */
	public static final String LDAP_SERVER_URL_PROP = "security-ldap-server-url";
	
	/**
	 * Indicates the based DN for LDAP authentication requests
	 * LDAP_BASEDN_NAME_PROP = "security-ldap-basedn"
	 * 
	 */
	public static final String LDAP_BASEDN_NAME_PROP = "security-ldap-base-dn";
	
	/**
	 * LDAP_PROXY_DN = "security-ldap-proxy-dn"
	 * 
	 * LDAP proxy user LDAP DN
	 */
	public static final String LDAP_PROXY_DN = "security-ldap-proxy-dn";
	
	/**
	
	 * LDAP_PROXY user password property
	 * LDAP_PROXY_PASSWORD = "security-ldap-proxy-password"
	 * 
	 */
	public static final String LDAP_PROXY_PASSWORD = "security-ldap-proxy-password";
	
	/**
	 * LDAP_UID_ATTRIBUTE = "security-ldap-uid-attribute"
	 * 
	 * ex: uid
	 */
	public static final String LDAP_UID_ATTRIBUTE = "security-ldap-uid-attribute";
}

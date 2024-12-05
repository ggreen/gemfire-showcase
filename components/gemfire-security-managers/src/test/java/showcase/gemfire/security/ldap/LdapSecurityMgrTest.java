package showcase.gemfire.security.ldap;

import nyla.solutions.core.ds.LDAP;
import nyla.solutions.core.ds.security.LdapSecurityGroup;
import nyla.solutions.core.ds.security.LdapSecurityUser;
import nyla.solutions.core.exception.ConfigException;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Cryption;
import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.ResourcePermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import showcase.gemfire.security.SecurityConstants;
import showcase.gemfire.security.exceptions.MissingSecurityProperty;

import javax.naming.NamingException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LdapSecurityMgrTest
{

	private static LDAP ldap;
	private static LDAPConnectionFactory ldapConnectionFactory ;

	private LdapSecurityMgr subject;
	private Properties props;

	@BeforeEach
	void setUp() throws NamingException {

		Config.reLoad();

		subject = new LdapSecurityMgr();
		subject.securityLogger = mock(org.apache.logging.log4j.Logger.class);
		ldap = mock(LDAP.class);


		ldapConnectionFactory = mock(LDAPConnectionFactory.class);

		when(ldapConnectionFactory.connect(any(), any(), any()))
				.thenReturn(ldap).thenReturn(ldap).thenReturn(ldap);


		subject.setLdapConnectionFactory(ldapConnectionFactory);

		props  = new Properties();
		props.setProperty("security-ldap-proxy-dn", "uid=admin");
		props.setProperty("security-ldap-proxy-password", "password");
		props.setProperty("security-ldap-server-url", "ldap://localhost:389");
		props.setProperty("security-ldap-group-attribute", "CN");
		props.setProperty("security-ldap-base-dn", "basedn");
		props.setProperty(LdapSecurityConstants.LDAP_MEMBEROF_ATTRIB_NM_PROP, "memberOf");

		props.setProperty("security-ldap-acl-user-admin", "ALL");
		props.setProperty("security-ldap-acl-user-multi", "CLUSTER:READ,DATA:READ,DATA:WRITE");
		props.setProperty("security-ldap-acl-user-guest", "NONE");
		props.setProperty("security-ldap-acl-user-readonly", "DATA:READ");
		props.setProperty("security-ldap-acl-group-readonly","DATA:READ");
		props.setProperty("security-ldap-acl-group-multiGroup","CLUSTER:READ,DATA:READ,DATA:WRITE");


		subject.setup(props);

		System.out.println("setup");

	}

	
	/**
	 * Testing the setup of the Ldap and making an initial connection
	 * @throws Exception when an unknown error occurs
	 */
	@Test
	public synchronized  void testSetup()
	throws Exception
	{
		LDAPConnectionFactory ldapConnectionFactory = mock(LDAPConnectionFactory.class);
		
		Properties props = new Properties();

		subject.setLdapConnectionFactory(ldapConnectionFactory);
		
		try
		{
			subject.setup(props);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
	
		props.setProperty("security-ldap-proxy-dn", "admin");

		try
		{
			subject.setup(props);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
		
		props.setProperty("security-ldap-proxy-password", "password");

		try
		{
			subject.setup(props);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
		
		props.setProperty("security-ldap-server", "ldap://localhost:389");
		try
		{
			subject.setup(props);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
		
		
		props.setProperty(LdapSecurityConstants.LDAP_MEMBEROF_ATTRIB_NM_PROP, "memberOf");
		try
		{
			subject.setup(props);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
		
		
		props.setProperty(LdapSecurityConstants.LDAP_SERVER_URL_PROP, "test");
		try
		{
			subject.setup(props);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
		
		props.setProperty(LdapSecurityConstants.LDAP_GROUP_ATTRIB_NM_PROP, "test");
		try
		{
			subject.setup(props);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
		
		props.setProperty("security-ldap-base-dn", "password");
		subject.setup(props);
		

	}
	@Test
	public void testSetupPassword() throws Exception
	{
		try
		{
			subject.setupProxyPassword(null);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
		

		Properties props = new Properties();

		String evnName = "SECURITY_LDAP_PROXY_PASSWORD";
		System.setProperty(evnName, "default");
		Config.reLoad();
		
		String password = subject.setupProxyPassword(props);
		
		assertEquals("default", password);
		
		props.setProperty(LdapSecurityConstants.LDAP_PROXY_PASSWORD, "secret");
		
		password = subject.setupProxyPassword(props);
		
		assertEquals("secret", password);
		
		Cryption cryption = new Cryption();
		
		String encryptedText = Cryption.CRYPTION_PREFIX+cryption.encryptText("secret");

		props.setProperty(LdapSecurityConstants.LDAP_PROXY_PASSWORD, encryptedText);
		
		password = subject.setupProxyPassword(props);
		
		assertEquals("secret", password);
	}

	@Test
	void authenticationException() {

		Properties properties = new Properties();
		properties.setProperty(SecurityConstants.USERNAME_PROP,"invalid");
		properties.setProperty(SecurityConstants.PASSWORD_PROP,"{cryption}invalid");
		try
		{
			var actual = subject.authenticate(properties);
			fail();
		}
		catch (AuthenticationFailedException e)
		{
			assertTrue(e.getMessage().contains("decryption error"),"Contains decryption error");
		}


	}

	@Test
	public synchronized void test_authenticate()
	throws Exception
	{
		synchronized (ldapConnectionFactory)
		{

			try
			{
				subject.authenticate(null);
				fail();
			}
			catch(AuthenticationFailedException e)
			{}
			
			try
			{
				subject.authenticate(new Properties());
				fail();
			}
			catch(AuthenticationFailedException e)
			{}
			
			Properties props = new Properties();
			props.setProperty("security-username", "admin");
			props.setProperty("security-password", "password");
			subject.authenticate(props);
			
			
			props.setProperty("security-username", "admin");
			props.setProperty("security-password", "{cryption}cndnirPoK+LecJOcWhnXmg==");
			subject.authenticate(props);
			
			
			
		}
		
	}
	@Test
	public void test_decrypt()
	throws Exception
	{
		byte[] keyBytes = Arrays.copyOf("GEDI-GEODI".getBytes(StandardCharsets.UTF_8), 16);
		
		Cryption c = new Cryption(keyBytes,"AES");
		String k = "{cryption}A0tyyt5QHch/hIinaQeKmw==";
		assertTrue(Cryption.isEncrypted(k));
		k = k.substring(Cryption.CRYPTION_PREFIX.length());
		assertEquals("A0tyyt5QHch/hIinaQeKmw==",k);
	
		assertEquals("cluster", c.decryptText(k));
		
	}
	@Test
	public void test_authentication_expired()
	throws Exception
	{
		Properties props = new Properties();
		props.setProperty("security-username", "nyla");
		props.setProperty("security-password", "alwaysworkhard");
		
		Properties initProps = new Properties();
		initProps.setProperty(SecurityConstants.LDAP_CACHING_EXPIRATION_MS_PROP, "sdsd");
		try
		{
			subject.setupLocalCredentialCaching(initProps );
			fail();
		}
		catch(ConfigException e)
		{
			System.out.println(e.getMessage());
		}
		
		initProps.setProperty(SecurityConstants.LDAP_CACHING_EXPIRATION_MS_PROP, "100");
		subject.setupLocalCredentialCaching(initProps );
		
		Principal principal = mock(Principal.class);
		
		subject.temporaryCacheCredentails(props,principal);
		assertNotNull(subject.validateCached(props));
		
		Thread.sleep(1005);
		
		assertNull(subject.validateCached(props));
		
	}
	
	@Test
	public void test_to_credential_key() throws Exception
	{

		Properties props = null;
		try{ subject.toCredentialKey(props); }catch(AuthenticationFailedException e) {}
		props = new Properties();
		try{ subject.toCredentialKey(props); }catch(AuthenticationFailedException e) {}
		
		props.setProperty("security-username", "imani");
		
		assertEquals("imani-",subject.toCredentialKey(props));
		
		props.remove("security-username");
		props.setProperty("security-password", "alwaysbekind");
		assertEquals("-alwaysbekind",subject.toCredentialKey(props));
		
		props.setProperty("security-username", "imani");
		assertEquals("imani-alwaysbekind",subject.toCredentialKey(props));
		
		
	}
	@Test
	public void test_authorize_user()
	throws Exception
	{
		System.setProperty("SECURITY_LDAP_ACL_USER_JUNIT", "ALL");

		Cryption.interpret("test".toCharArray());

		setUp();
		
		synchronized (ldapConnectionFactory)
		{
			
			ResourcePermission permission  = new ResourcePermission("DATA", "READ");
			
			String dn ="uid=junit";
			LdapSecurityUser user = new LdapSecurityUser("junit",dn);
			
			assertTrue(subject.authorize(user, permission));
			
			 dn = "uid=admin";
			user = new LdapSecurityUser("admin",dn);
			

			boolean bool = subject.authorize(user, permission);
			assertTrue(bool);
			user = new LdapSecurityUser("unknown","uid=unknown");
			bool = subject.authorize(user, permission);
			assertFalse(bool);
			
			 permission  = new ResourcePermission("DATA", "READ");
			 user = new LdapSecurityUser("readonly","uid=readonly");
			  bool = subject.authorize(user, permission);
			  assertTrue(bool);
		}
		
	}
	@Test
	public void test_authorize_group()
	throws Exception
	{

		synchronized (ldapConnectionFactory)
		{
			
			LdapSecurityUser user = new LdapSecurityUser("unknown","uid=unknown");
			user.addGroup(new LdapSecurityGroup("readonly", "uid=readonly"));
			ResourcePermission permission  = new ResourcePermission("DATA", "READ");
			boolean bool = subject.authorize(user, permission);
			assertTrue(bool);
			user = new LdapSecurityUser("unknown","uid=unknown");
			bool = subject.authorize(user, permission);
			assertFalse(bool);
			
			 permission  = new ResourcePermission("DATA", "READ");
			 user = new LdapSecurityUser("readonly","uid=readonly");
			  bool = subject.authorize(user, permission);
			  assertTrue(bool);
		}
		
	}
	

	@Test
	public void test_MultiplePermissions() throws Exception
	{

		synchronized (ldapConnectionFactory)
		{
			
			LdapSecurityUser user = new LdapSecurityUser("multi","uid=multi");
			user.addGroup(new LdapSecurityGroup("myGroup", "uid=readonly"));
			ResourcePermission permission1  = new ResourcePermission("DATA", "READ");
			boolean bool = subject.authorize(user, permission1);
			assertTrue(bool);
			
			ResourcePermission permission2  = new ResourcePermission("CLUSTER", "READ");
			 user = new LdapSecurityUser("multi","uid=multi");
			  bool = subject.authorize(user, permission2);
			  assertTrue(bool);
			  
			  System.out.println(new ResourcePermission("DATA", "WRITE"));
			  
			  assertTrue(subject.authorize(user, new ResourcePermission("DATA", "WRITE")));
			  assertFalse(subject.authorize(user, new ResourcePermission("CLUSTER", "MANAGE")));

		}
		
	}
	
	@Test
	public void test_MultipleGropuPermissions() throws Exception
	{

		synchronized (ldapConnectionFactory)
		{
			
			LdapSecurityUser user = new LdapSecurityUser("multiGroupUser","uid=multi");
			user.addGroup(new LdapSecurityGroup("multiGroup", "uid=readonly"));
			ResourcePermission permission1  = new ResourcePermission("DATA", "READ");
			boolean bool = subject.authorize(user, permission1);
			assertTrue(bool);
			
			ResourcePermission permission2  = new ResourcePermission("CLUSTER", "READ");
			  bool = subject.authorize(user, permission2);
			  assertTrue(bool);
			  
			  System.out.println(new ResourcePermission("DATA", "WRITE"));
			  
			  assertTrue(subject.authorize(user, new ResourcePermission("DATA", "WRITE")));
			  assertFalse(subject.authorize(user, new ResourcePermission("CLUSTER", "MANAGE")));

		}
		
	}

}
	
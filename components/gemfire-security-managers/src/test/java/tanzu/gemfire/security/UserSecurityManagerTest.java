package tanzu.gemfire.security;

import nyla.solutions.core.exception.SystemException;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Cryption;
import nyla.solutions.core.util.settings.ConfigSettings;
import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.ResourcePermission;
import org.apache.geode.security.ResourcePermission.Operation;
import org.apache.geode.security.ResourcePermission.Resource;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tanzu.gemfire.security.settings.SettingsUserService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("deprecation")
public class UserSecurityManagerTest
{
	private UserSecurityManager subject;
	private User user;
	private SettingsUserService userService;
	private Cryption cryption = new Cryption();

	@BeforeAll
	static void beforeAll()
	{
		System.setProperty(Cryption.CRYPTION_KEY_PROP, "PIVOTAL-ALWAYS-BE-KIND");
		System.setProperty(Config.SYS_PROPERTY, "src/test/resources/gemfire_users.properties");
		Config.reLoad();
	}

	@BeforeEach
	void setUp() {

	}

	@Test
	public void test_CanAuthorizeCluster()
	throws Exception
	{
		user = new User();
		user.setUserName("testUser");
		user.setEncryptedPassword(new Cryption().encryptText("admin").getBytes(StandardCharsets.UTF_8));
		user.setPriviledges(Collections.singleton("ALL"));
		
		String password = new Cryption().encryptText("password");

		System.setProperty("gemfire.security-users.testUser", Cryption.CRYPTION_PREFIX+password+",ALL,[priviledge],[,priviledge]");
		
		userService = new SettingsUserService();
		subject = new UserSecurityManager(userService);
		
		Logger logWriter = mock(Logger.class);
		
		subject.setLogger(logWriter);
		
		
		ResourcePermission none = new ResourcePermission();
		
		assertTrue(subject.authorize(user, none));
		
		user.setPriviledges(Collections.singleton("NULL"));
		ResourcePermission clusterManager = new ResourcePermission(Resource.CLUSTER,Operation.MANAGE);
		assertTrue(!subject.authorize(user, clusterManager));
		
		
		user.setPriviledges(Collections.singleton("CLUSTER"));
		assertTrue(subject.authorize(user, clusterManager));
		
		user.setPriviledges(Collections.singleton("CLUSTER:MANAGE"));
		assertTrue(subject.authorize(user, clusterManager));
		
		user.setPriviledges(Collections.singleton("CLUSTER:READ"));
		assertTrue(!subject.authorize(user, clusterManager));
		
		
		user.setPriviledges(Collections.singleton("CLUSTER:READ"));
		assertTrue(!subject.authorize(user, clusterManager));
		
		
		user.setPriviledges(Arrays.asList("CLUSTER:READ","CLUSTER:MANAGE"));
		assertTrue(subject.authorize(user, clusterManager));
		
		
		
		ResourcePermission clusterRead  = new ResourcePermission(Resource.CLUSTER,Operation.READ);
		
		user.setPriviledges(Arrays.asList("CLUSTER:MANAGE"));
		assertTrue(!subject.authorize(user, clusterRead));
		
		
		user.setPriviledges(Arrays.asList("CLUSTER:READ"));
		assertTrue(subject.authorize(user, clusterRead));
	}
	

	@Test
	public void test_CanAuthorizeData()
	throws Exception
	{
		User user = new User();
		user.setUserName("testUser");
		user.setEncryptedPassword(new Cryption().encryptText("admin").getBytes(StandardCharsets.UTF_8));
		user.setPriviledges(Collections.singleton("ALL"));
		
		String password = new Cryption().encryptText("password");

		System.setProperty("gemfire.security-users.testUser", Cryption.CRYPTION_PREFIX+password+",ALL,[priviledge],[,priviledge]");
		
		userService = new SettingsUserService();
		 subject = new UserSecurityManager(userService);
		
		Logger logger = mock(Logger.class);
		subject.setLogger(logger);
		

		ResourcePermission dataManager = new ResourcePermission(Resource.DATA,Operation.MANAGE);
		assertTrue(subject.authorize(user, dataManager));
		
		user.setPriviledges(Collections.singleton("DATA"));
		assertTrue(subject.authorize(user, dataManager));
		
		user.setPriviledges(Collections.singleton("DATA:MANAGE"));
		assertTrue(subject.authorize(user, dataManager));
		
		user.setPriviledges(Collections.singleton("DATA:READ"));
		assertTrue(!subject.authorize(user, dataManager));
		
		
		user.setPriviledges(Arrays.asList("DATA:READ","DATA:MANAGE"));
		assertTrue(subject.authorize(user, dataManager));
		
		
		ResourcePermission clusterRead  = new ResourcePermission(Resource.DATA,Operation.READ);
		
		user.setPriviledges(Arrays.asList("DATA:MANAGE"));
		assertTrue(!subject.authorize(user, clusterRead));
		
		
		user.setPriviledges(Arrays.asList("DATA:READ"));
		assertTrue(subject.authorize(user, clusterRead));
	}

	@SuppressWarnings({ "unchecked" })
	@Test
	public void test_UserCanAuthenticate()
	throws Exception
	{		
		User user = new User();
		String adminUserName = "admin";
		user.setUserName(adminUserName);
		user.setEncryptedPassword(new Cryption().encryptText("admin").getBytes(StandardCharsets.UTF_8));

		UserService userService = mock(UserService.class);
		when(userService.findUser(adminUserName)).thenReturn(user);
		 subject = new UserSecurityManager(userService);
		
		try
		{ 
			subject.authenticate(null);
			fail();
		}
		catch(AuthenticationFailedException e)
		{
		}
		
		Properties credentails = new Properties();
		
		try
		{ 
			subject.authenticate(credentails);
			fail();
		}
		catch(AuthenticationFailedException e)
		{
		}
		
		 credentails.setProperty("security-username", "invalid");
		 
			try
			{ 
				subject.authenticate(credentails);
				fail();
			}
			catch(AuthenticationFailedException e)
			{
			}
			
			
			credentails.setProperty("security-username", "admin");
			
			try
			{ 
				subject.authenticate(credentails);
				fail();
			}
			catch(AuthenticationFailedException e)
			{
			}
			
			
			
			credentails.setProperty("security-password", new Cryption().encryptText("invalid"));
			
			try
			{ 
				subject.authenticate(credentails);
				fail();
			}
			catch(AuthenticationFailedException e)
			{
			}
			
			credentails.setProperty("security-password", new Cryption().encryptText("admin"));
			
			Object principal = subject.authenticate(credentails);
			
			assertNotNull(principal);
			
			
			credentails.setProperty("security-password", Cryption.CRYPTION_PREFIX+new Cryption().encryptText("admin"));
			
			principal = subject.authenticate(credentails);
			
			assertNotNull(principal);
			
			credentails.setProperty("security-password", " "+Cryption.CRYPTION_PREFIX+new Cryption().encryptText("admin"));
			
			principal = subject.authenticate(credentails);
			
			assertNotNull(principal);
			
			//test with auth init
			credentails.setProperty("security-username","admin");
			credentails.setProperty("security-password","admin");
			ConfigAuthInitialize auth = new ConfigAuthInitialize();
			Properties authInit = auth.getCredentials(credentails);
			
			principal = subject.authenticate(authInit);
			
			assertNotNull(principal);
		
			//test with auth init encrypted
			credentails.setProperty("security-username","admin");
			credentails.setProperty("security-password","{cryption}"+
					Cryption.removePrefix(cryption.encryptText("admin")));
			
			authInit = auth.getCredentials(credentails);
			
			principal = subject.authenticate(authInit);
			
			assertNotNull(principal);
			
			
			credentails.setProperty("security-password", "invalid");
			
			try
			{ 
				subject.authenticate(credentails);
				fail();
			}
			catch(AuthenticationFailedException e)
			{
				
			}
			credentails.setProperty("security-password", "admin");
			
			principal = subject.authenticate(credentails);
			
			assertNotNull(principal);
	}
	@Test
	public void test_encrypted_password()
	throws Exception
	{
		ConfigSettings settings = new ConfigSettings();
		System.setProperty(Cryption.CRYPTION_KEY_PROP, "PIVOTAL-ALWAYS-BE-KIND");
		System.setProperty(Config.SYS_PROPERTY, "src/test/resources/gemfire_users.properties");
		Config.reLoad();
		settings.reLoad();
		
		Thread.sleep(100);
		subject = new UserSecurityManager(new SettingsUserService(settings));
		Properties props = new Properties();
		Cryption cryption = new Cryption();
		
		String passwordTest = cryption.encryptText("admin");
		assertEquals("admin",cryption.decryptText(passwordTest));
		
		props.setProperty(SecurityConstants.USERNAME_PROP, "admin");
		props.setProperty(SecurityConstants.PASSWORD_PROP, "{cryption}"+cryption.encryptText("admin"));
		
		assertEquals("admin",cryption.decryptText(props.getProperty(SecurityConstants.PASSWORD_PROP)));
		
		assertNotNull(subject.authenticate(props));
	}

	@Test
	public void test_encrypted_password_no_prefix()
			throws Exception
	{
		ConfigSettings settings = new ConfigSettings();
		System.setProperty(Cryption.CRYPTION_KEY_PROP, "PIVOTAL-ALWAYS-BE-KIND");
		System.setProperty(Config.SYS_PROPERTY, "src/test/resources/gemfire_users.properties");
		Config.reLoad();
		settings.reLoad();

		Thread.sleep(100);
		subject = new UserSecurityManager(new SettingsUserService(settings));
		Properties props = new Properties();
		Cryption cryption = new Cryption();

		String passwordTest = cryption.encryptText("admin");
		assertEquals("admin",cryption.decryptText(passwordTest));

		props.setProperty(SecurityConstants.USERNAME_PROP, "admin");
		props.setProperty(SecurityConstants.PASSWORD_PROP, cryption.encryptText("admin"));

		assertEquals("admin",cryption.decryptText(props.getProperty(SecurityConstants.PASSWORD_PROP)));

		assertNotNull(subject.authenticate(props));
	}
	
	@Test
	public void test_unencrypted_password()
	throws Exception
	{
		System.setProperty(Cryption.CRYPTION_KEY_PROP, "PIVOTAL-ALWAYS-BE-KIND");
		System.setProperty(Config.SYS_PROPERTY, "src/test/resources/gemfire_users.properties");
		Config.reLoad();
		ConfigSettings settings = new ConfigSettings();

		subject = new UserSecurityManager(new SettingsUserService(settings));
		Properties props = new Properties();
				
		props.setProperty(SecurityConstants.USERNAME_PROP, "admin");
		props.setProperty(SecurityConstants.PASSWORD_PROP, "wrong");
		
		try
		{
			subject.authenticate(props);
			fail();
		}
		catch(AuthenticationFailedException e)
		{
			System.out.println("SUCCESS");
		}
	}
	
	@Test
	public void test_do_not_include_encrypted_password() throws Exception
	{
		ConfigSettings settings = new ConfigSettings();
		System.setProperty(Cryption.CRYPTION_KEY_PROP, "PIVOTAL-ALWAYS-BE-KIND");
		System.setProperty(Config.SYS_PROPERTY, "src/test/resources/gemfire_users.properties");
		subject = new UserSecurityManager(new SettingsUserService(settings));
		Properties props = new Properties();
		props.setProperty(SecurityConstants.USERNAME_PROP, "admin");
		props.setProperty(SecurityConstants.PASSWORD_PROP, "admin");
		Object principal = subject.authenticate(props);
		
		assertTrue(subject.authorize(principal, new ResourcePermission("CLUSTER","WRITE")));
		
		assertTrue(!principal.toString().contains("encryptedPassword"));
	}
	
	@Test
	public void test_must_not_throw_nylaexceptions() throws Exception
	{
		SettingsUserService service = mock(SettingsUserService.class);
		subject = new UserSecurityManager(service);
		validateException(subject,null);
		
		when(service.findUser(anyString())).thenThrow(new SystemException());
		
		Properties props = new Properties();
		validateException(subject,props);
		
		props.setProperty(SecurityConstants.USERNAME_PROP, "nyla");
		props.setProperty(SecurityConstants.PASSWORD_PROP, "nope");
		validateException(subject,props);
		
		
		props.setProperty(SecurityConstants.USERNAME_PROP, "admin");
		props.setProperty(SecurityConstants.PASSWORD_PROP, Cryption.CRYPTION_PREFIX+"invalid");
		validateException(subject,props);
		
	}
	
	private void validateException(UserSecurityManager mgr, Properties prop)
	{
	    try
		{	
			mgr.authenticate(prop);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			assertFalse(e.getClass().getName().contains("nyla"));
			if(e.getCause() != null)
				assertFalse(e.getCause().getClass().getName().contains("nyla"));
			
		}
	}
}

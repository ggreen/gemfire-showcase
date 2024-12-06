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
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("deprecation")
public class UserSecurityManagerTest
{
	@BeforeAll
	static void beforeAll()
	{
		System.setProperty(Cryption.CRYPTION_KEY_PROP, "PIVOTAL");
		System.setProperty(Config.SYS_PROPERTY, "src/test/resources/geode_users.properties");
		Config.reLoad();
	}

	@Test
	public void test_CanAuthorizeCluster()
	throws Exception
	{
		User user = new User();
		user.setUserName("testUser");
		user.setEncryptedPassword(new Cryption().encryptText("admin").getBytes(StandardCharsets.UTF_8));
		user.setPriviledges(Collections.singleton("ALL"));
		
		String password = new Cryption().encryptText("password");

		System.setProperty("gemfire.security-users.testUser", Cryption.CRYPTION_PREFIX+password+",ALL,[priviledge],[,priviledge]");
		
		UserService userService = new SettingsUserService();
		UserSecurityManager mgr = new UserSecurityManager(userService);
		
		Logger logWriter = mock(Logger.class);
		
		mgr.setLogger(logWriter);
		
		
		ResourcePermission none = new ResourcePermission();
		
		assertTrue(mgr.authorize(user, none));
		
		user.setPriviledges(Collections.singleton("NULL"));
		ResourcePermission clusterManager = new ResourcePermission(Resource.CLUSTER,Operation.MANAGE);
		assertTrue(!mgr.authorize(user, clusterManager));
		
		
		user.setPriviledges(Collections.singleton("CLUSTER"));
		assertTrue(mgr.authorize(user, clusterManager));
		
		user.setPriviledges(Collections.singleton("CLUSTER:MANAGE"));
		assertTrue(mgr.authorize(user, clusterManager));
		
		user.setPriviledges(Collections.singleton("CLUSTER:READ"));
		assertTrue(!mgr.authorize(user, clusterManager));
		
		
		user.setPriviledges(Collections.singleton("CLUSTER:READ"));
		assertTrue(!mgr.authorize(user, clusterManager));
		
		
		user.setPriviledges(Arrays.asList("CLUSTER:READ","CLUSTER:MANAGE"));
		assertTrue(mgr.authorize(user, clusterManager));
		
		
		
		ResourcePermission clusterRead  = new ResourcePermission(Resource.CLUSTER,Operation.READ);
		
		user.setPriviledges(Arrays.asList("CLUSTER:MANAGE"));
		assertTrue(!mgr.authorize(user, clusterRead));
		
		
		user.setPriviledges(Arrays.asList("CLUSTER:READ"));
		assertTrue(mgr.authorize(user, clusterRead));
	}//------------------------------------------------
	

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
		
		UserService userService = new SettingsUserService();
		UserSecurityManager mgr = new UserSecurityManager(userService);
		
		Logger logger = mock(Logger.class);
		mgr.setLogger(logger);
		

		ResourcePermission dataManager = new ResourcePermission(Resource.DATA,Operation.MANAGE);
		assertTrue(mgr.authorize(user, dataManager));
		
		user.setPriviledges(Collections.singleton("DATA"));
		assertTrue(mgr.authorize(user, dataManager));
		
		user.setPriviledges(Collections.singleton("DATA:MANAGE"));
		assertTrue(mgr.authorize(user, dataManager));
		
		user.setPriviledges(Collections.singleton("DATA:READ"));
		assertTrue(!mgr.authorize(user, dataManager));
		
		
		user.setPriviledges(Arrays.asList("DATA:READ","DATA:MANAGE"));
		assertTrue(mgr.authorize(user, dataManager));
		
		
		ResourcePermission clusterRead  = new ResourcePermission(Resource.DATA,Operation.READ);
		
		user.setPriviledges(Arrays.asList("DATA:MANAGE"));
		assertTrue(!mgr.authorize(user, clusterRead));
		
		
		user.setPriviledges(Arrays.asList("DATA:READ"));
		assertTrue(mgr.authorize(user, clusterRead));
	}//------------------------------------------------

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
		UserSecurityManager mgr = new UserSecurityManager(userService);
		
		try
		{ 
			mgr.authenticate(null);
			fail();
		}
		catch(AuthenticationFailedException e)
		{
		}
		
		Properties credentails = new Properties();
		
		try
		{ 
			mgr.authenticate(credentails);
			fail();
		}
		catch(AuthenticationFailedException e)
		{
		}
		
		 credentails.setProperty("security-username", "invalid");
		 
			try
			{ 
				mgr.authenticate(credentails);
				fail();
			}
			catch(AuthenticationFailedException e)
			{
			}
			
			
			credentails.setProperty("security-username", "admin");
			
			try
			{ 
				mgr.authenticate(credentails);
				fail();
			}
			catch(AuthenticationFailedException e)
			{
			}
			
			
			
			credentails.setProperty("security-password", new Cryption().encryptText("invalid"));
			
			try
			{ 
				mgr.authenticate(credentails);
				fail();
			}
			catch(AuthenticationFailedException e)
			{
			}
			
			credentails.setProperty("security-password", new Cryption().encryptText("admin"));
			
			Object principal = mgr.authenticate(credentails);
			
			assertNotNull(principal);
			
			
			credentails.setProperty("security-password", Cryption.CRYPTION_PREFIX+new Cryption().encryptText("admin"));
			
			principal = mgr.authenticate(credentails);
			
			assertNotNull(principal);
			
			credentails.setProperty("security-password", " "+Cryption.CRYPTION_PREFIX+new Cryption().encryptText("admin"));
			
			principal = mgr.authenticate(credentails);
			
			assertNotNull(principal);
			
			//test with auth init
			credentails.setProperty("security-username","admin");
			credentails.setProperty("security-password","admin");
			ConfigAuthInitialize auth = new ConfigAuthInitialize();
			Properties authInit = auth.getCredentials(credentails);
			
			principal = mgr.authenticate(authInit);
			
			assertNotNull(principal);
		
			//test with auth init encrypted
			credentails.setProperty("security-username","admin");
			credentails.setProperty("security-password","{cryption}cndnirPoK+LecJOcWhnXmg==");
			
			authInit = auth.getCredentials(credentails);
			
			principal = mgr.authenticate(authInit);
			
			assertNotNull(principal);
			
			
			credentails.setProperty("security-password", "invalid");
			
			try
			{ 
				mgr.authenticate(credentails);
				fail();
			}
			catch(AuthenticationFailedException e)
			{
				
			}
			credentails.setProperty("security-password", "admin");
			
			principal = mgr.authenticate(credentails);
			
			assertNotNull(principal);
	}//------------------------------------------------
	@Test
	public void test_encrypted_password()
	throws Exception
	{
		ConfigSettings settings = new ConfigSettings();
		System.setProperty(Cryption.CRYPTION_KEY_PROP, "PIVOTAL");
		System.setProperty(Config.SYS_PROPERTY, "src/test/resources/geode_users.properties");
		Config.reLoad();
		settings.reLoad();
		
		Thread.sleep(100);
		UserSecurityManager mgr = new UserSecurityManager(new SettingsUserService(settings));
		Properties props = new Properties();
		Cryption cryption = new Cryption();
		
		String passwordTest = cryption.encryptText("admin");
		assertEquals("admin",cryption.decryptText(passwordTest));
		
		props.setProperty(SecurityConstants.USERNAME_PROP, "admin");
		props.setProperty(SecurityConstants.PASSWORD_PROP, "{cryption}"+cryption.encryptText("admin"));
		
		assertEquals("admin",cryption.decryptText(props.getProperty(SecurityConstants.PASSWORD_PROP)));
		
		assertNotNull(mgr.authenticate(props));
	}//------------------------------------------------
	
	@Test
	public void test_unencrypted_password()
	throws Exception
	{
		System.setProperty(Cryption.CRYPTION_KEY_PROP, "PIVOTAL");
		System.setProperty(Config.SYS_PROPERTY, "src/test/resources/geode_users.properties");
		Config.reLoad();
		ConfigSettings settings = new ConfigSettings();

		UserSecurityManager mgr = new UserSecurityManager(new SettingsUserService(settings));
		Properties props = new Properties();
				
		props.setProperty(SecurityConstants.USERNAME_PROP, "admin");
		props.setProperty(SecurityConstants.PASSWORD_PROP, "wrong");
		
		try
		{
			mgr.authenticate(props);
			fail();
		}
		catch(AuthenticationFailedException e)
		{
			System.out.println("SUCCESS");
		}
	}//------------------------------------------------
	
	@Test
	public void test_do_not_include_encrypted_password() throws Exception
	{
		ConfigSettings settings = new ConfigSettings();
		System.setProperty(Cryption.CRYPTION_KEY_PROP, "PIVOTAL");
		System.setProperty(Config.SYS_PROPERTY, "src/test/resources/geode_users.properties");
		UserSecurityManager mgr = new UserSecurityManager(new SettingsUserService(settings));
		Properties props = new Properties();
		props.setProperty(SecurityConstants.USERNAME_PROP, "admin");
		props.setProperty(SecurityConstants.PASSWORD_PROP, "admin");
		Object principal = mgr.authenticate(props);
		
		assertTrue(mgr.authorize(principal, new ResourcePermission("CLUSTER","WRITE")));
		
		assertTrue(!principal.toString().contains("encryptedPassword"));
	}
	
	@Test
	public void test_must_not_throw_nylaexceptions() throws Exception
	{
		SettingsUserService service = mock(SettingsUserService.class);
		UserSecurityManager mgr = new UserSecurityManager(service);
		validateException(mgr,null);
		
		when(service.findUser(anyString())).thenThrow(new SystemException());
		
		Properties props = new Properties();
		validateException(mgr,props);
		
		props.setProperty(SecurityConstants.USERNAME_PROP, "nyla");
		props.setProperty(SecurityConstants.PASSWORD_PROP, "nope");
		validateException(mgr,props);
		
		
		props.setProperty(SecurityConstants.USERNAME_PROP, "admin");
		props.setProperty(SecurityConstants.PASSWORD_PROP, Cryption.CRYPTION_PREFIX+"invalid");
		validateException(mgr,props);
		
	}//------------------------------------------------
	
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
	}//------------------------------------------------
}

package io.pivotal.dataTx.geode.security;

import nyla.solutions.core.util.Cryption;
import nyla.solutions.core.util.settings.Settings;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ConfiguredUserCacheLoaderTest
{

	@Test
	public void testCreate_HasUsers()
	throws Exception
	{
		Settings settings = mock(Settings.class);
		
		Properties properties = new Properties();
		
		when(settings.getProperties()).thenReturn(properties);
		
		String encryptedPassword = new Cryption().encryptText("password");
		
		String nylaProperty = Cryption.CRYPTION_PREFIX+encryptedPassword+",admin,read, write ";
		
		properties.setProperty("security-users.nyla", nylaProperty);
		
		ConfiguredUserCacheLoader cacheLoader = new ConfiguredUserCacheLoader(settings);
		
		assertNotNull(cacheLoader);
		User user = cacheLoader.findUser("invalid");
		
		assertNull(user);
		
		user = cacheLoader.findUser("nyla");
		
		assertNotNull(user);
		assertEquals(user.getUserName(),"nyla");
		
		assertTrue(Arrays.equals(user.getEncryptedPassword(), encryptedPassword.getBytes(StandardCharsets.UTF_8)));
		
		assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("admin")); 
		assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("read"));
		assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("write"));
		
		
	}
	
	@Test
	public void testCreate_UnEncryptedPAsswodHasUsers()
	throws Exception
	{		
		Settings settings = mock(Settings.class);
		
		Properties properties = new Properties();

		when(settings.getProperties()).thenReturn(properties);
		
		String encryptedPassword = new Cryption().encryptText("password");
		
		String nylaProperty = Cryption.CRYPTION_PREFIX+encryptedPassword+",admin,read, write ";
		
		properties.setProperty("security-users.nyla", nylaProperty);
		
		ConfiguredUserCacheLoader cacheLoader = new ConfiguredUserCacheLoader(settings);
		
		assertNotNull(cacheLoader);
		User user = cacheLoader.findUser("invalid");
		
		assertNull(user);
		
		user = cacheLoader.findUser("nyla");
		
		assertNotNull(user);
		assertEquals(user.getUserName(),"nyla");
		
		assertTrue(Arrays.equals(user.getEncryptedPassword(), encryptedPassword.getBytes(StandardCharsets.UTF_8)));
		
		assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("admin")); 
		assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("read"));
		assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("write"));
		
		
	}
	@Test
	public void testGemFirePropertiesCreate_HasUsers()
	throws Exception
	{		
		Settings settings = mock(Settings.class);
		
		Properties properties = new Properties();

		when(settings.getProperties()).thenReturn(properties);
		
		String encryptedPassword = new Cryption().encryptText("password");
		
		String nylaProperty = Cryption.CRYPTION_PREFIX+encryptedPassword+",admin,read, write ";
		
		properties.setProperty("gemfire.security-users.nyla", nylaProperty);
		
		ConfiguredUserCacheLoader cacheLoader = new ConfiguredUserCacheLoader(settings);
		
		assertNotNull(cacheLoader);
		User user = cacheLoader.findUser("invalid");
		
		assertNull(user);
		
		user = cacheLoader.findUser("nyla");
		
		assertNotNull(user);
		assertEquals(user.getUserName(),"nyla");
		
		assertTrue(Arrays.equals(user.getEncryptedPassword(), encryptedPassword.getBytes(StandardCharsets.UTF_8)));
		
		assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("admin")); 
		assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("read"));
		assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("write"));
		
		
	}
}

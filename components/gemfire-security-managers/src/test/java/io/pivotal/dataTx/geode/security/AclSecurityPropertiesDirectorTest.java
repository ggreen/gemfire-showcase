package io.pivotal.dataTx.geode.security;


import io.pivotal.dataTx.geode.security.ldap.LdapSecurityConstants;
import nyla.solutions.core.util.Config;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class AclSecurityPropertiesDirectorTest
{

	static Map<String,String> builtUsersMap = new HashMap<String,String>();
	static Map<String,String> builtGroupMap = new HashMap<String,String>();
	
	@Test
	public void testConstruct()
	{
		System.setProperty("config.properties", "src/test/resources/config.properties");
		Config.reLoad();
		
		String group_prefix =LdapSecurityConstants.LDAP_ACL_GROUP_PREFIX;
		String user_prefix = LdapSecurityConstants.LDAP_ACL_USER_PREFIX;
		Properties props = new Properties();
		
		String systemUserPropertName = Config.sanitizeEnvVarNAme(user_prefix)+"JUNIT";
		String systemGroupPropertName = Config.sanitizeEnvVarNAme(group_prefix)+"JUNITGroups";
		
		System.setProperty(systemUserPropertName, "ALL");
		System.setProperty(systemGroupPropertName, "ALL");
		Config.reLoad();
		
		assertNotNull(Config.getProperties().get(systemUserPropertName));
		
		SecurityAclBuilder builder = new SecurityAclBuilder()
		{
			@Override
			public void buildUserPermission(String user, String permission)
			{
				System.out.println("user:"+user);
				builtUsersMap.put(user,permission); 
				
			}
			@Override
			public void buildGroupPermission(String group, String permission)
			{
				builtGroupMap.put(group,permission);
			}
		};
		
		
		
		AclSecurityPropertiesDirector d = new AclSecurityPropertiesDirector
		(props, group_prefix, user_prefix);
		
		d.construct(builder);
		
		assertTrue(!builtUsersMap.isEmpty());
		assertTrue(!builtGroupMap.isEmpty());
		
		assertTrue(builtUsersMap.containsKey("cluster"));
		assertTrue(builtUsersMap.containsKey("readonly"));
		assertTrue(builtGroupMap.containsKey("reporters"));
	}
	
	

}

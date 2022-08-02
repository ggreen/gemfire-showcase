package com.vmware.data.services.apache.geode.functions;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

import org.apache.geode.security.ResourcePermission;
import org.junit.jupiter.api.*;

import nyla.solutions.core.security.data.SecurityAccessControl;
import nyla.solutions.core.security.data.SecurityPermission;
import nyla.solutions.core.security.user.data.UserProfile;

public class DemoSecuredFunctionTest
{

	@Test
	public void test()
	{
		
		UserProfile principal = new UserProfile();
		principal.setId("test");
		
		SecurityAccessControl userAcl = new SecurityAccessControl(principal, "DATA:READ:Sidecache_Test");
		userAcl.addPermission(new SecurityPermission("DATA:WRITE:Sidecache_Test"));
		
		DemoSecuredFunction f = new DemoSecuredFunction();
		
		Collection<ResourcePermission> funcPermissions = (Collection<ResourcePermission>)f.getRequiredPermissions("Sidecache_Test");
		System.out.println("collection:"+funcPermissions);
		assertNotNull(funcPermissions);
		
		
		assertTrue(userAcl.checkPermission(new SecurityPermission("DATA:READ:Sidecache_Test")));
		assertTrue(userAcl.checkPermission(new SecurityPermission("DATA:WRITE:Sidecache_Test")));
		assertTrue(!userAcl.checkPermission(new SecurityPermission("DATA:WRITE")));
		
		for (ResourcePermission resourcePermission : funcPermissions)
		{
			assertTrue(userAcl.checkPermission(new SecurityPermission(resourcePermission.toString())),"has:"+resourcePermission.toString());
		}
		
	}

}

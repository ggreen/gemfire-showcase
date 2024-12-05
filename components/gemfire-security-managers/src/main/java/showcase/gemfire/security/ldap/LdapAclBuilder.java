package showcase.gemfire.security.ldap;

import showcase.gemfire.security.SecurityAclBuilder;
import nyla.solutions.core.ds.security.LdapSecurityGroup;
import nyla.solutions.core.ds.security.LdapSecurityUser;
import nyla.solutions.core.security.data.AllPermission;
import nyla.solutions.core.security.data.SecurityAcl;
import nyla.solutions.core.security.data.SecurityPermissionContains;

public class LdapAclBuilder extends SecurityAclBuilder
{
	private SecurityAcl acl = new SecurityAcl();
	
	public void buildGroupPermission(String group, String permission)
	{
		LdapSecurityGroup principal = new LdapSecurityGroup(group);
		
		if ("ALL".equals(permission) || "*".equals(permission))

			this.acl.addEntry(null, principal, new AllPermission());

		else

			this.acl.addEntry(null, principal, new SecurityPermissionContains(permission));
		
	}
	
	public void buildUserPermission(String user, String permission)
	{
		LdapSecurityUser principal = new LdapSecurityUser(user);
		
		if ("ALL".equals(permission) || "*".equals(permission))

			this.acl.addEntry(null, principal, new AllPermission());

		else

			this.acl.addEntry(null, principal, new SecurityPermissionContains(permission));
		
		
	}
     SecurityAcl getAcl()
	{
		return this.acl;
	}

}

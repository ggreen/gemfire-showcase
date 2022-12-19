package io.pivotal.dataTx.geode.security;

import nyla.solutions.core.patterns.creational.BuilderDirector;
import nyla.solutions.core.util.Config;

import java.util.Map;
import java.util.Properties;


/**
 * <pre>
 * This class implements the builder design pattern for
 * creating an access control list based on text properties.
 * 
 * Example File create defines three groups 1) admin_grp 2) readonly_cluster_grp 3) readonly_grp
 * and two users 1) data_read_user 2) data_write_user
 * 
 * 
 *  security-ldap-acl-group-admin_grp=ALL
 *  security-ldap-acl-group-readonly_cluster_grp=CLUSTER:READ
 *  security-ldap-acl-group-readonly_grp=READ
 *  security-ldap-acl-user-data_read_user=DATA:READ
 *  security-ldap-acl-user-data_write_user=DATA:WRITE
 *  
 *  Supports getting user/group permissions for configuration/environment variables
 *  
 *  export SECURITY_LDAP_ACL_GROUP_ADMIN_GRP=ALL
 *  export SECURITY_LDAP_ACL_GROUP_READONLY_CLUSTER_GRP=CLUSTER:READ
 *  export SECURITY_LDAP_ACL_GROUP_READONLY_GRP=READ
 *  export SECURITY_LDAP_ACL_USER_DATA_READ_USER=DATA:READ
 *  export SECURITY_LDAP_ACL_USER_DATA_WRITE_USER=DATA:WRITE
 *  
 *  </pre>
 * @author Gregory Green
 *
 */
public class AclSecurityPropertiesDirector implements BuilderDirector<SecurityAclBuilder>
{
	private final Properties securityProps;

	
	private final String group_prefix;
	private final String user_prefix;

	
	public AclSecurityPropertiesDirector(
				Properties securityProps, 
				String group_prefix,
				String user_prefix)
	{
		super();
		this.securityProps = securityProps;
		
		this.group_prefix = group_prefix;
		this.user_prefix = user_prefix;
	}//------------------------------------------------


	public void construct(SecurityAclBuilder builder)
	{
		String envUserPrefix = Config.sanitizeEnvVarNAme(user_prefix);
		String envGroupPrefix = Config.sanitizeEnvVarNAme(group_prefix);
		
		securityProps.forEach(
		(k, v) ->
		{
			String keyText = k.toString();
			if (keyText.startsWith(group_prefix) || keyText.startsWith(envGroupPrefix))
			{
				String group = keyText.substring(group_prefix.length());
				builder.buildGroupPermission(group, v.toString());
			}
			else if (keyText.startsWith(user_prefix) || keyText.startsWith(envUserPrefix))
			{
				String user = keyText.substring(user_prefix.length());
				builder.buildUserPermission(user, v.toString());
			}
		});
		
		
	
		
		Map<Object,Object> properties = Config.getProperties();
		
		if(properties == null ||  properties.isEmpty())
			return;
		
		properties.forEach((k, v) ->
		{
			String keyText = k.toString();
			
			if (keyText.startsWith(envGroupPrefix) || keyText.startsWith(group_prefix))
			{
				String group = keyText.substring(envGroupPrefix.length());
				builder.buildGroupPermission(group, v.toString());
			}
			else if (keyText.startsWith(envUserPrefix)|| keyText.startsWith(user_prefix))
			{
				String user = keyText.substring(envUserPrefix.length());
				builder.buildUserPermission(user, v.toString());
			}

		});
		

	}

}

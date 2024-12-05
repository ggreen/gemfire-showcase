# Overview

This module contains an LDAP based implementation of the GemFire [security manager](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-security-custom_security_manager_example.html).

Note this package supports the following

- [GemFire](https://tanzu.vmware.com/gemfire) version 10.0.0 or higher
- [nyla.solutions.core](https://github.com/nyla-solutions/nyla/tree/master) version 2.2.3 or higher.
- Java JDK 17 or higher


## Cluster Startup

1) Set the CRYPTION_KEY environment variable used for encrypting/decrypting passwords prior to starting the cluster

```shell
export CRYPTION_KEY=PIVOTAL
```

You should assert that the CRYPTION_KEY value is a minimum of 16 characters.

2) Setup GemFire Security Property File

The following is an example security property file content (ex: gfldapsecurity.properties)


```properties
# LDAP PROXY user DN used to for all authentication LDAP request
security-ldap-proxy-dn=uid=admin,ou=system

# LDAP PROXY user password (encrypted or un-encrypted passwords supported) 
security-ldap-proxy-password=secret

# LDAP server URL
security-ldap-server-url=ldap://localhost:389

# LDAP base dn to search for user for authentication reques
security-ldap-base-dn=ou=system

# LDAP attribute that will match the user ID
security-ldap-uid-attribute=uid

# The LDAP  attribute the indicates the users' group associations
security-ldap-memberOf-attribute=memberOf

# The LDAP GROUP attribute that will match the security-ldap-acl-group-${??} property
security-ldap-group-attribute=CN

# Example Access Control Lists
# user nyla has permission to read data

security-ldap-acl-user-nyla=DATA:READ
		
# user cluster has permission to performance any cluster operation
security-ldap-acl-user-cluster=CLUSTER

# user admin ALL permissions
security-ldap-acl-user-admin=ALL
security-ldap-acl-group-administrator=ALL


# User credentials used to join the GemFire cluster
security-username=cluster
security-password={cryption}ENCRYPTED
```




**ACL Permissions**

The Access Control List (ACL) permissions the property file are based on the GemFire ResourePermission (Resource:Operation). The format of the property are **security-ldap-acl-user-${UID}** or  **security-ldap-acl-group-${groupID}**.

The following is an example file content

```properties
# Users
security-ldap-acl-user.<userName1>=[privilege] [,privilege]* 
security-ldap-acl-user.<userName2>=[privilege] [,privilege]* 

# Groups
security-ldap-acl-group-${groupID1}=[privilege] [,privilege]*
security-ldap-acl-group-${groupID2}=[privilege] [,privilege]*
```


The following are example ACLs permissions privilege

- ALL - admin level user access with no restrictions
- CLUSTER - all cluster read, write and manage permissions
- CLUSTER:READ - cluster read permission
- CLUSTER:WRITE - cluster write permission
- CLUSTER:MANAGE - cluster management permissions such as shutdown cluster and members
- DATA - all data read, write and manage permissions
- DATA:READ - data read permission
- DATA:WRITE - data write permission
- DATA:MANAGE - data managed permissions such as creating regions
- DATA:WRITE:<RegionName> - - data write permission for a region 
- DATA:READ:<RegionName> - - data read permission for a region 
- DATA:MANAGE:<RegionName> - - data manage permission for a region 
- DATA:WRITE:<RegionName>:<KEY - data write permission for a region entry that matches a string key 
- DATA:WRITE:<RegionName>:<KEY - - data read permission for a region entry that matches a string key 




In the following file content property example, the "cluster" LDAP user is given both CLUSTER:READ and CLUSTER:MANAGE permissions.

```properties
security-ldap-acl-user-cluster=CLUSTER:READ,CLUSTER:MANAGE
```

See the following are all supported permissions: 

[GemFire Authorization](https://docs.vmware.com/en/VMware-GemFire/10.1/gf/managing-security-implementing_authorization.html)


## Password Encryption Support

You can use the [security-password-app](../../applications/security/security-password-app) to generate an encrypted password. 

Usage java -DCRYPTION_KEY=<HASH -jar dataTx-geode-password-encryption-app-2.0.0.jar  <pass>

Example:

```shell
java -DCRYPTION_KEY=MYSALT -jar applications/security/security-password-app/build/libs/security-password-app-0.0.1-SNAPSHOT.jar mypassword	
{cryption}OUTPUT_EXAMPLE
```

The encrypted password is always prefixed with {cryption}. This prefixed should be included in the property passwords.

3) **Start the Locators**

The following are example gfsh commands to start a single locator

```shell
cd $GEMFIRE_HOME/bin
export PROJECT_ROOT=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase

$GEMFIRE_HOME/bin/gfsh -e "start locator --name=local  --J=-DCRYPTION_KEY=PIVOTAL-ALWAYS-BE-KIND --http-service-bind-address=localhost --disable-classloader-isolation=true --classpath=$PROJECT_ROOT/components/gemfire-security-managers/build/libs/gemfire-security-managers-2.0.1-SNAPSHOT.jar:$PROJECT_ROOT/applications/libs/nyla.solutions.core-2.2.3.jar --enable-cluster-configuration  --http-service-port=7070 --security-properties-file=$PROJECT_ROOT/components/gemfire-security-managers/src/main/resources/ldap/gfldapsecurity.properties --J=-Dgemfire.security-manager=showcase.gemfire.security.ldap.LdapSecurityMgr   --connect=false"
```

 Note, it is recommended to replace --J=-DCRYPTION_KEY=PIVOTAL with setting an environment variable (ex: export CRYPTION_KEY=MYSALT) for added security protection. This will provide user for know the cryption salt by inspecting the arguments to the GemFire process.
 
	
4) **Start Servers**

The following are example gfsh commands to start two data node cache servers

```shell
cd $GEMFIRE_HOME/bin
export PROJECT_ROOT=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase

$GEMFIRE_HOME/bin/gfsh -e "start server --name=server1   --J=-DCRYPTION_KEY=PIVOTAL-ALWAYS-BE-KIND --use-cluster-configuration=true --server-port=10001 --http-service-port=7071 --locators=localhost[10334]  --disable-classloader-isolation=true --classpath=$PROJECT_ROOT/components/gemfire-security-managers/build/libs/gemfire-security-managers-2.0.1-SNAPSHOT.jar:$PROJECT_ROOT/applications/libs/nyla.solutions.core-2.2.3.jar   --security-properties-file=$PROJECT_ROOT/components/gemfire-security-managers/src/main/resources/ldap/gfldapsecurity.properties --J=-Dgemfire.security-manager=showcase.gemfire.security.ldap.LdapSecurityMgr"
```

Stop Server 2
```shell
cd $GEMFIRE_HOME/bin
export PROJECT_ROOT=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase

$GEMFIRE_HOME/bin/gfsh -e "start server --name=server2  --J=-DCRYPTION_KEY=PIVOTAL-ALWAYS-BE-KIND --use-cluster-configuration=true --server-port=10002  --http-service-port=7072  --locators=localhost[10334] --disable-classloader-isolation=true --classpath=$PROJECT_ROOT/components/gemfire-security-managers/build/libs/gemfire-security-managers-2.0.1-SNAPSHOT.jar:$PROJECT_ROOT/applications/libs/nyla.solutions.core-2.2.3.jar   --security-properties-file=$PROJECT_ROOT/components/gemfire-security-managers/src/main/resources/ldap/gfldapsecurity.properties  --J=-Dgemfire.security-manager=showcase.gemfire.security.ldap.LdapSecurityMgr"
```
		
	
Note, it is recommended to replace --J=-DCRYPTION_KEY=PIVOTAL-ALWAYS-BE-KIND with setting an environment variable (ex: export CRYPTION_KEY=MYSALT) and to use encrypted passwords.
 
After startup, gfsh and pulse will require a username/password to connect.



** LDAP SSL **


When using  both LDAPS and enables  GemFire SSL components like JMX, you should set the LDAP_USE_SSL_CONFIG_FACTORY to true in the environment variables.

LDAP properties

| Property                     | Notes                                                                                                             |
|------------------------------|-------------------------------------------------------------------------------------------------------------------|
| LDAP_USE_SSL_CONFIG_FACTORY  | (**true** or **false**)  Boolean value to determine if LDAPS is used with the following configurations properties |
| LDAP_SSL_KEYSTORE            | The SSL KEYSTORE file path location                                                                               |
| LDAP_SSL_TRUSTSTORE          | The SSL KEYSTORE file path location                                                                               |
| LDAP_SSL_KEYSTORE_PASSWORD   | The password for the key store                                                                                    |
| LDAP_SSL_TRUSTSTORE_PASSWORD | The password for the trust store                                                                                  |

EXAMPLE

```shell
export LDAP_USE_SSL_CONFIG_FACTORY=true
```
    

You will also need to set the following in the setenv, based on a separate keystore
to used for LDAP.

```shell
export LDAP_SSL-KEYSTORE=...
export LDAP-SSL-TRUSTSTORE=..
export LDAP-SSL-KEYSTORE-PASSWORD=..
export LDAP-SSL-TRUSTSTORE-PASSSWORD=..
```
    

# Local Integration LDAP Testings

## Setup LDAP 

For local testing, it is recommended to use [ApacheDS](http://directory.apache.org/apacheds/).


```shell
cd /Users/devtools/repositories/ds/ApacheDS/apacheds-2.0.0.AM28-SNAPSHOT/
./bin/apacheds.sh default start
```

The Apache DS will be available on port 10389. 
The default user/password is admin/secret.

```properties
uid=admin,ou=system
password=secret
port=10389
```

You can use  [ApacheDS Studio](http://directory.apache.org/studio/) the add users for testing.

Also see the following the scripts build the docker image, start Apache DS and add test LDAP users

- src/test/resources/ldap/build.sh 
- src/test/resources/ldap/start.sh  
- src/test/resources/ldap/addusers.sh

  
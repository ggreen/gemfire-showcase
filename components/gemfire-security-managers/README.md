# GemFire Security Managers

This project provides a simple file or LDAP based Security Manager 
capabilities for an [GemFire](https://tanzu.vmware.com/gemfire)/[Apache Geode cluster](https://geode.apache.org/).

This implementation is derived from other [open source framework](https://github.com/nyla-solutions/gedi-geode-security-extensions).


This reference implementation security manager has been proven to work in
production for several GemFire deployments.


Supported Security Extensions

**1 - [LDAP](README_LDAP_SecurityMgr.md)** 

 - User or Group level permissions
 - Password encryption in-flight 
 - Password encryption at rest
 - Supports LDAP user role/group permissions caching for the life of the GemFire session
 - LDAPS/TLS/SSL
 - Supports LDAP authentication caching with a timeout to survive LDAP network hiccups and optimizations when using  the GemFire REST API.

**2- [File Properties](README.md#property-file-security-manager)**

 - User level permissions
 - Password encryption in-flight 
 - Password encryption at rest


# Installation Notes

**Building the Java Artifacts**

You will need a maven installation to build the Java artifacts.  Directions
are available on the [maven website](http://maven.apache.org/download.cgi). After that, 
follow these instructions to build all of the Java artifacts.


## Setting `CRYPTION_KEY` configuration

The encryption and decryption of a user's password is based on an encryption key.

You must set the CRYPTION_KEY environment variable or as a JVM system property on each started Geode member (locators and cache server).
   
    export CRYPTION_KEY=<MY.ENCRYPTION.KEY.HERE>
    
Example

    export CRYPTION_KEY=PIVOTAL-ALWAYS-BE-KIND


---------------------------------------------------
# LDAP Security Manager

See [LDAP Security manager](README_LDAP_SecurityMgr.md)

---------------------------------------------------
  
# Property File Security Manager

The class [tanzu.gemfire.security.UserSecurityManager](src/main/java/tanzu/gemfire/security/UserSecurityManager.java) implementation property file based security implementation. 
The following explains how to deploy the file properties based user implementation of the
security manager.

Set the GemFire security property **security-manager**=*tanzu.gemfire.security.UserSecurityManager* 


 
**Starting the Locator**

The following is an example gfsh command to start the locator


The key is setting the Gemfire property `security-manager=tanzu.gemfire.security.UserSecurityManager`. Note the jar nyla.solutions.core-VERSION.jar and `dataTx-geode-security-extensions-VERSION.jar` jars must be added to the CLASSPATH.
 

Start Locator 

```shell
cd $GEMFIRE_HOME/bin
export PROJECT_ROOT=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase
$GEMFIRE_HOME/bin/gfsh -e "start locator --name=locator --J=-DCRYPTION_KEY=PIVOTAL-ALWAYS-BE-KIND  --J=-Dconfig.properties=$PROJECT_ROOT/components/gemfire-security-managers/src/test/resources/gemfire_users.properties  --J=-Dgemfire.security-manager=tanzu.gemfire.security.UserSecurityManager --J=-Dgemfire.jmx-manager-start=true --disable-classloader-isolation=true --classpath=$PROJECT_ROOT/components/gemfire-security-managers/build/libs/gemfire-security-managers-3.0.0.jar:$PROJECT_ROOT/applications/libs/nyla.solutions.core-2.2.3.jar --enable-cluster-configuration --locators=localhost[10334]   --connect=false --security-properties-file=$PROJECT_ROOT/components/gemfire-security-managers/src/test/resources/settings/user-security.properties --J=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8880"
```

Start  Servers

Server 1

```shell
cd $GEMFIRE_HOME/bin
export PROJECT_ROOT=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase
$GEMFIRE_HOME/bin/gfsh -e "start server --name=server1   --J=-DCRYPTION_KEY=PIVOTAL-ALWAYS-BE-KIND --use-cluster-configuration=true --server-port=10001 --http-service-port=7071 --locators=localhost[10334]  --disable-classloader-isolation=true --classpath=$PROJECT_ROOT/components/gemfire-security-managers/build/libs/gemfire-security-managers-3.0.0.jar:$PROJECT_ROOT/applications/libs/nyla.solutions.core-2.2.3.jar    --J=-Dconfig.properties=$PROJECT_ROOT/components/gemfire-security-managers/src/test/resources/gemfire_users.properties --J=-Dgemfire.security-manager=tanzu.gemfire.security.UserSecurityManager  --security-properties-file=$PROJECT_ROOT/components/gemfire-security-managers/src/test/resources/settings/user-security.properties --J=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8881"
``` 

Server 
```shell
cd $GEMFIRE_HOME/bin
export PROJECT_ROOT=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase
$GEMFIRE_HOME/bin/gfsh -e "start server --name=server2   --J=-DCRYPTION_KEY=PIVOTAL-ALWAYS-BE-KIND --use-cluster-configuration=true --server-port=10002 --http-service-port=7072 --locators=localhost[10334]  --disable-classloader-isolation=true --classpath=$PROJECT_ROOT/components/gemfire-security-managers/build/libs/gemfire-security-managers-3.0.0.jar:$PROJECT_ROOT/applications/libs/nyla.solutions.core-2.2.3.jar    --J=-Dconfig.properties=$PROJECT_ROOT/components/gemfire-security-managers/src/test/resources/gemfire_users.properties --J=-Dgemfire.security-manager=tanzu.gemfire.security.UserSecurityManager  --security-properties-file=$PROJECT_ROOT/components/gemfire-security-managers/src/test/resources/settings/user-security.properties --J=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8882"
``` 

 
*Note it is recommended to use encrypted passwords*. 
  
**Configured Users**


You can pass a **config.properties** JVM property to set a file that contains the security users passwords/privileges.

The following is an example to set this JVM property using gfsh --J option.

```shell
--J=-Dconfig.properties=$PROJECT_ROOT/components/gemfire-security-managers/src/test/resources/gemfire_users.properties
```

The following is an example file content

```properties
# Frequency to check for configuration changes (change this requires a restart)
CONFIG_FILE_WATCH_POLLING_INTERVAL_MS=60000


# First user
gemfire.security-users.<userName1>=userEncryptedPassword,[privilege] [,privilege]* 
    
# Second user
 gemfire.security-users.<userName2>=userEncryptedPassword,[privilege] [,privilege]* 
```


The following is an example default setting for an **admin** user with the **ALL** privilege and password:admin with in encrypted format when  `CRYPTION_KEY=PIVOTAL-ALWAYS-BE-KIND`

```properties
gemfire.security-users.admin={cryption}.....,ALL

```


You can also add the following GemFire security property to configure users thru system properties

```shell
-Dgemfire.security-users.<userName1>=userEncryptedPassword,[privilege] [,privilege]* 
-Dgemfire.security-users.<userName2>=userEncryptedPassword,[privilege] [,privilege]* 
```

*Example System Property:*

```shell
-Dgemfire.security-users.admin={cryption}....,ALL
```
     

### Privilege

The User privileges are based on the GemFire ResourePermission (Resource:Operation).

- ALL - admin level user access with no restrictions
- CLUSTER - all cluster read, write and manage permissions
- CLUSTER:READ - cluster read permission
- CLUSTER:WRITE - cluster write permission
- CLUSTER:MANAGE - cluster management permissions such as shutdown cluster and members
- DATA - all data read, write and manage permissions
- DATA:READ - data read permission
- DATA:WRITE - data write permission
- DATA:MANAGE - data managed permissions such as creating regions
- DATA:WRITE:*RegionName* - - data write permission for a region 
- DATA:READ:*RegionName* - - data read permission for a region 
- DATA:MANAGE:*RegionName* - - data manage permission for a region 
- DATA:WRITE:*RegionName*:*KEY* - data write permission for a region entry that matches a string key 
- DATA:WRITE:*RegionName*:*KEY* - - data read permission for a region entry that matches a string key 



See the following for more details on GemFire permissions.
[GemFire Resource Permissions](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-security-implementing_authorization.html)




# Encryption Password

Use the following sample command to encrypt a password. NOTE: CRYPTION_KEY variable must match the value set on the server.

Usage:

```shell
java -DCRYPTION_KEY=PIVOTAL-ALWAYS-BE-KIND -jar applications/security/security-password-app/build/libs/security-password-app-0.0.1-SNAPSHOT.jar mypassword
```
     

Example:

    /Projects/Pivotal/dataTx/dev/gemfire/security/dataTx-geode-security-mgr-extensions/security-password-app$ java -jar target/dataTx-geode-password-encryption-app-2.0.0.jar mypassword
    {cryption}se2323scsdlYDEzaE1sds=

**Start Cache Server --password encryption**

When starting a cache server the --user=... and ---password=... must be provided to authenticate to the locator. The password can be provided in encrypted or unencrypted. 

*Note it is recommended to encrypt all passwords*.

**User passwords encryption** 

All user passwords in a property file or passed in as system properties must be encrypted.
 
 

# GemFire Client Connections

GemFire clients will provide an implementation of org.apache.geode.security.AuthInitialize.
The security-username and security-password must initialize as GemFire properties.

See the following link for details:

[GemFire Security Manager](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-security-authentication_overview.html)


Note that the security-password can be encrypted or unencrypted.


# Building Notes

## Install GPG Tools (optional)

Install the GPG at the following URL

    https://gpgtools.org/

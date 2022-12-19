# DataTx Geode Security Extensions

This project provides a simple file or LDAP based Security Manager 
capabilities for a [Apache Geode](https://pivotal.io/pivotal-gemfire)/[Apache Geode cluster](https://geode.apache.org/).

This implementation is derived from other [open source framework](https://github.com/nyla-solutions/gedi-geode-security-extensions).

It is managed by the Data Transformation team (DataTx) 
from [Pivotal](http://www.pivotal.io) Services.


Supported Security Extensions

**1 - [LDAP](https://github.com/pivotalservices/dataTx-geode-security-mgr-extensions/blob/master/security-core/README_LDAP_SecurityMgr.md)** 

 - User or Group level permissions
 - Password encryption in-flight 
 - Password encryption at rest
 - Supports LDAP user role/group permissions caching for the life of the GemFire session
 - LDAPS/TLS/SSL
 - Supports LDAP authentication caching with a timeout to survive LDAP network hiccups and optimizations when using  the GemFire REST API.

**2- [File Properties](https://github.com/pivotalservices/dataTx-geode-security-mgr-extensions)**

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

    export CRYPTION_KEY=PIVOTAL


---------------------------------------------------
# LDAP Security Manager

See [LDAP Security manager](README_LDAP_SecurityMgr.md)

---------------------------------------------------
  
# Property File Security Manager

The class [io.pivotal.dataTx.geode.security.UserSecurityManager](https://github.com/pivotalservices/dataTx-geode-security-mgr-extensions/blob/master/security-core/src/main/java/io/pivotal/dataTx/geode/security/UserSecurityManager.java) implementation property file based security implementation. The following explains how to deploy the file properties based user implementation of the
security manager.

Set the GemFire security property **security-manager**=*io.pivotal.dataTx.geode.security.UserSecurityManager* 


 
**Starting the Locator**

The following is an example gfsh command to start the locator


The key is setting the Gemfire property `security-manager=io.pivotal.dataTx.geode.security.UserSecurityManager`. Note the jar nyla.solutions.core-VERSION.jar and `dataTx-geode-security-extensions-VERSION.jar` jars must be added to the CLASSPATH.
 
 Inside of gfsh execute the following
 
 
    start locator --name=locator --J="-DCRYPTION_KEY=PIVOTAL"  --J="-Dconfig.properties=/Projects/Pivotal/dataTx/dev/gemfire/security/dataTx-geode-security-mgr-extensions/security-core/src/test/resources/geode_users.properties"  --J=-Dgemfire.security-manager=io.pivotal.dataTx.geode.security.UserSecurityManager --J=-Dgemfire.jmx-manager-start=true --classpath=/Projects/Pivotal/dataTx/dev/gemfire/security/dataTx-geode-security-mgr-extensions/security-core/target/dataTx-geode-security-extensions-2.0.0.jar:/Projects/Pivotal/dataTx/dev/gemfire/security/dataTx-geode-security-mgr-extensions/lib/nyla.solutions.core-1.2.4.jar --enable-cluster-configuration --locators=localhost[10334]  --connect=false
    
The following is an example gfsh command to start two servers where the JARS and properties must be set similar to the locator.
    
    start server --name=server1 --locators=localhost[10334] --server-port=10201  --use-cluster-configuration=true --J="-DCRYPTION_KEY=PIVOTAL"  --J="-Dconfig.properties=/Projects/Pivotal/dataTx/dev/gemfire/security/dataTx-geode-security-mgr-extensions/security-core/src/test/resources/geode_users.properties" --user=admin --password="admin" --J=-Dgemfire.security-manager=io.pivotal.dataTx.geode.security.UserSecurityManager  --classpath=/Projects/Pivotal/dataTx/dev/gemfire/security/dataTx-geode-security-mgr-extensions/security-core/target/dataTx-geode-security-extensions-2.0.0.jar:/Projects/Pivotal/dataTx/dev/gemfire/security/dataTx-geode-security-mgr-extensions/lib/nyla.solutions.core-1.2.4.jar
    
    start server --name=server2 --locators=localhost[10334] --server-port=10202  --use-cluster-configuration=true --J="-DCRYPTION_KEY=PIVOTAL"  --J="-Dconfig.properties=/Projects/Pivotal/dataTx/dev/gemfire/security/dataTx-geode-security-mgr-extensions/security-core/src/test/resources/geode_users.properties" --user=admin --password="admin" --J=-Dgemfire.security-manager=io.pivotal.dataTx.geode.security.UserSecurityManager  --classpath=/Projects/Pivotal/dataTx/dev/gemfire/security/dataTx-geode-security-mgr-extensions/security-core/target/dataTx-geode-security-extensions-2.0.0.jar:/Projects/Pivotal/dataTx/dev/gemfire/security/dataTx-geode-security-mgr-extensions/lib/nyla.solutions.core-1.2.4.jar
    
 *Note it is recommended to use encrypted passwords*. 
  
**Configured Users**


You can pass a **config.properties** JVM property to set a file that contains the security users passwords/privileges.

The following is an example to set this JVM property using gfsh --J option.


    --J="-Dconfig.properties=/Projects/Pivotal/dataTx/dev/gemfire/security/dataTx-geode-security-mgr-extensions/security-core/src/test/resources/geode_users.properties"

The following is an example file content

```properties
# Frequency to check for configuration changes (change this requires a restart)
CONFIG_FILE_WATCH_POLLING_INTERVAL_MS=60000


# First user
gemfire.security-users.<userName1>=userEncryptedPassword,[privilege] [,privilege]* 
    
# Second user
 gemfire.security-users.<userName2>=userEncryptedPassword,[privilege] [,privilege]* 
```


The following is an example default setting for an **admin** user with the **ALL** privilege and password:admin with in encrypted format when  `CRYPTION_KEY=PIVOTAL`

```properties

gemfire.security-users.admin={cryption}.....,ALL

```


You can also add the following GemFire security property to configure users thru system properties

    -Dgemfire.security-users.<userName1>=userEncryptedPassword,[privilege] [,privilege]* 
    
    -Dgemfire.security-users.<userName2>=userEncryptedPassword,[privilege] [,privilege]* 
    
*Example System Property:*

     -Dgemfire.security-users.admin={cryption}....,ALL
     
     

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
- DATA:WRITE:<RegionName> - - data write permission for a region 
- DATA:READ:<RegionName> - - data read permission for a region 
- DATA:MANAGE:<RegionName> - - data manage permission for a region 
- DATA:WRITE:<RegionName>:<KEY - data write permission for a region entry that matches a string key 
- DATA:WRITE:<RegionName>:<KEY - - data read permission for a region entry that matches a string key 



See the following for more details on GemFire permissions.
[http://gemfire.docs.pivotal.io/geode/managing/security/implementing_authorization.html](http://gemfire.docs.pivotal.io/geode/managing/security/implementing_authorization.html)




# Encryption Password

Use the following sample command to encrypt a password. NOTE: CRYPTION_KEY variable must match the value set on the server.

Usage:

     java -jar target/dataTx-geode-password-encryption-app-2.0.0.jar mypassword

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

[https://gemfire.docs.pivotal.io/geode/managing/security/implementing_authentication.html](https://gemfire.docs.pivotal.io/geode/managing/security/implementing_authentication.html)


Note that the security-password can be encrypted or unencrypted.




 
# GemFire Commercial Repository


See the following for instruction to down the GemFire artifacts.

    https://gemfire.docs.pivotal.io/gemfire/getting_started/installation/obtain_gemfire_maven.html

# Building Notes

## Install GPG Tools (optional)

Install the GPG at the following URL

    https://gpgtools.org/

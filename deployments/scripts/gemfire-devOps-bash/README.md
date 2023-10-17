# Apache Geode DevOps Bash

The **gemfire-devOps-bash** project is a set of open source scripts to automate the management of [GemFire](https://www.vmware.com/products/gemfire.html) using bash UNIX scripts.

*Features*

- Docker images for locator and cache Servers
- Example Kubernetes (k8) service deployment
- Security implementations
  -  properties files with user/privileges
  -  LDAP and Property file base security Manager
- SSL/TLS key store creation and configuration
- Password encryption configuration



The following provides a summary of the main scripts.

| Name                   | description                                                               |
|------------------------|---------------------------------------------------------------------------|
| backupDiskStores.sh    | Example backup for disk stores using the gfsh backup disk-store           |
| clean.sh               | Clean files and directories in the member's working directory             |
| compactDiskStores.sh   | Uses the gfsh compact disk-store command (schedule daily off peak)        |
| configPdx.sh           | Setup PDX to support read-serialized=true with a disk store               |
| copyLibs.sh	           | Copy lib file changes across cluster members                              |
| encryptPassword.sh     | Generates an encrypted password                                           |
| exportAllData.sh       | Exports all region data for each member in parallel                       |
| encryptPassword.sh     | Generate an encrypted password to stored in variables                     |
| gem-downloads.sh       | Reference script for automating Geode/JDK tar ball S3/http download       |
| gem-install.sh         | Reference script for install of Geode/JDK from downloaded tar balls       |
| gfsh.sh                | Script that starts the gfsh shell                                         |
| importRegionExports.sh | Script that imports all region data exported by exportAllData.sh          |
| killMember.sh          | Kills all Geode JVM processes running on the current server               |
| listMemberStatus.sh    | Runs the gfsh list members command                                        |
| listRegions.sh         | list all regions in a cluster                                             | 
| remoteCleanAll.sh      | Runs clean.sh on all members in the cluster                               |
| remoteKillMembers.sh   | Executes killMember.sh on locators and datanodes                          |
| remoteScript.sh        | Wrapper for executing scripts remotely using ssh                          |
| remoteStartCluster.sh  | Starts cluster with all locators and data nodes                           |
| remoteSyncScripts.sh   | Copies needed scripts (ex: startDataNode.sh) to cluster members           |
| selfSignCert.sh        | Script generates a self signed security keys using JDK keytool            |
| setup_root_dir.sh      | Setup root install directory and set owners as the $GEM_USER in setenv.sh |
| showRegionMetrics.sh   | Execute the gfsh show metric command for a given region                   |
| setenv.sh              | Required to set cluster settings (see examples in env_templates)          |
| shutdown.sh            | Uses the Geode gfsh shutdown to stop members gracefully                   |
| startDataNode.sh       | Starts a cache server data node on the local server                       |
| startLocator.sh        | Starts a locator on the local server                                      |
| stopLocator.sh         | Stop the local running locator                                            |
| stopDataNode.sh        | Stop the local running data node                                          |
| tailDataNodeLogs.sh    | tail the local data node logs                                             |
| tailLocatorLogs.sh     | tail the local locator logs                                               |

### Security


This framework uses the open source dataTx-geode-security-extensions implementation to support a Geode [integrated security manager](https://geode.apache.org/docs/guide/113/managing/security/implementing_security.html).

See [https://github.com/pivotalservices/dataTx-geode-security-mgr-extensions](https://github.com/pivotalservices/dataTx-geode-security-mgr-extensions) for more information.


## Configuration

The scripts expects a *setenv.sh* file to be in the current working directory or root directory of where the scripts are located.

The directory [env_templates](https://github.com/vmwarepivotallabs/dataTx-gemfire-devOps-bash/tree/master/env_templates) directory file contains example *setenv.sh* scripts. These scripts contain all the needed variables to automate the management and installation of a Geode cluster.

### Editing configurations

Copy the needed template for the env_templates to the same directory where the remoteStartCluster. The file name must be *setenv.sh*.

- $ cp env_templates/setenv_aws.sh setenv.sh
- Manually modify the setenv as needed

# Installation

Prerequisite

    sudo yum install wget

Execute the

    mkdir config

Put list of locators and data nodes one server per line

        vi config/dataNodes
        vi config/locators
        vi config/remoteLocators


If LDAP security is enabled, update the *config/ldapUsers.properties* file (see security documentation below)

    touch config/ldapUsers.properties


or if user file based security is enabled, then update the *config/users.properties* file

  touch config/users.properties


Execute

    sudo ./setup_root_dir.sh

Note this installs both Geode and Java. The Java install is a generic LINUX installation zip. If you experience issues with the downloaded Java version, please use an alternate method for installing it on your servers.

Update the JAVA_HOME in your setenv.sh as needed.



**Remote Locators for WAN Replications**

When connecting two or more clusters with WAN replication, put the list locators in the opposite data center in the file config/remoteLocators. One locator per line.

*REMINDER*:  Switch the  DISTRIBUTED_ID the another cluster


For example Data Center 1

    export DISTRIBUTED_ID=1
    export REMOTE_DISTRIBUTED_ID=2

For example Data Center 2

      export DISTRIBUTED_ID=2
      export REMOTE_DISTRIBUTED_ID=1

DC1: copy DC2's config/locators to config/remoteLocators
DC2: copy DC1's config/remoteLocators to config/locators


Run the following commands to complete the installation

    ./gem-downloads.sh
    ./gem-install.sh
    export CERT_PASSWORD=YOURPASSWORDHERE
    ./selfSignCert.sh
    ./remoteInstall.sh


## Starting/Stopping Servers

### Cluster Start

Uses the *removeStartCluster.sh* to start the cluster.

 Set config/locators and config/dataNodes files to configure the list of
 servers that will runs these members.


JAVA and Geode must be installed on each locator and data node. Geode export GEMFIRE_HOME=$GEMFIRE_INSTALL_DIR/$GEMFIRE_FOLDER_NM must point to a valid install. JAVA_HOME in *setenv.sh* must also be correctly reflected.

    ./remoteStartCluster.sh


### Cluster Shutdown

Use the *shutdown.sh* script to gratefully shutdown the cluster.

Always gracefully shutdown the cluster to prevent potential start up issues and or
disk store corruption issues.


## Operations

### gfsh

Apache Geode gfsh (pronounced "gee-fish") provides a single  command-line interface. You can launch, manage, and monitor Apache Geode processes, data, and applications.
For more information on what you can do with gfsh please see the reference documentation available at https://geode.apache.org/docs/guide/113/tools_modules/gfsh/chapter_overview.html)

Gfsh can be run from the command line, ex:

Use the *gfsh.sh* to launch gfsh.

You must connect to the cluster

```shell script
  gfsh>connect --locator=$LOCATOR_HOST[$LOCATOR_PORT]
```

**Useful gfsh commands**

Command             | Notes
--------------------|-------------------------------------------------------------
gfsh>connect        | This command establishes a connection to a locator.
gfsh>list members   | This command lists all components in the distributed system.
gfsh>list regions   | This command lists all regions in the distributed system.
gfsh>list functions | List Functions if deployed


**Used gfsh to export Configurations**

To save your configuration and data, you can use the “export config”  commands to save your settings as files that you can later copy the configuration files into the working directories of other members or use for troubleshooting the underlying configurations.

 For example:

    gfsh>export config ...
    Downloading Cache XML file: /home/user/./server1-cache.xml
    Downloading properties file: /home/user/./server1-gf.properties
    Downloading Cache XML file: /home/user/./server2-cache.xml
    Downloading properties file: /home/user/./server2-gf.properties


### Export/Import DATA

Use *exportAllData.sh* to save data. By default the data for all region is
saved to the $BACKUP_DIR set in the *setenv.sh*

You can also directly use the gfsh>export data command. To export server region data, use the export data command.

  For example:
  gfsh>export data --region=region1 --file=region1.gfd --member=server1
  You can later use the import data command to import that data into the same region on another member.

Use the *importRegionExports.sh* to import data that was previously exported to the *exportAllData.sh* script.



## Setenv variables


**User Settings**

| Variables           | Notes                          | Examples            |
|---------------------|--------------------------------|---------------------|
| export GEM_USER     | The Geode service account user | gemfire             |
| export SSH_IDENTITY | the SSH/SCP -i identify        | " -i ~/aws/env.pem" |



**Installation Settings**

| Variables                  | Notes                                                                 | Examples                                         |
|----------------------------|-----------------------------------------------------------------------|--------------------------------------------------|
| export ROOT_DIR            | The root directory for installation                                   | /opt/pivotal                                     |
| export DOWNLOAD_DIR        | the software download locations used by gem-install                   | $ROOT_DIR/download                               |
| export INSTALL_DIR         | The installation direcoty                                             | $ROOT_DIR                                        |
| export RUNTIME_DIR         | The runtime directory root  directoru                                 | $ROOT_DIR/runtime                                |
| export JAVA_INSTALL        | The gem-install root Java installation directory                      | $INSTALL_DIR/java                                |
| export JAVA_FOLDER_NM      | The JDK tar ball name when installing                                 | jdk1.8.0_181                                     |
| export JAVA_HOME           | The Java home                                                         | $JAVA_INSTALL/$JAVA_FOLDER_NM                    |
| export GEMFIRE_INSTALL_DIR | The directory where Geode will be installed                           | $ROOT_DIR/gemfire                                |
| export S3_ROOT             | THE HTTP root for the gem-download to find the JDK and Geode tar zips | https://s3.us-east-2.amazonaws.com/geode-gemfire |
| export JDK_TAR_BALL        | The JAva tar ball file name at the S3/ http ROOT                      | jdk-8u181-linux-x64.tar.gz                       | |
| export GEM_TAR_BALL        | The Geode tar ball name                                               |                                                  |
| export GEMFIRE_FOLDER_NM   | The GemFire folder in the tar ball                                    |                                                  |



**Backup directory**

| Variables         | Notes | Examples |
|-------------------|---------| ------------- |
| export BACKUP_DIR |The backup directory|$SOFTWARE_DIR/backup |

**SSL/Security Settings**

| Variables                 | Notes                                                                                            | Examples              |
|---------------------------|--------------------------------------------------------------------------------------------------|-----------------------|
| export SECURITY_DIR       | The security directory                                                                           | $INSTALL_DIR/security |
| export CERT_PASSWORD      | The self signed password (could be put in the secured environment variable for greater security) |
| export CERT_VALIDITY_DAYS | How long the self signed cert is valid                                                           | 3600                  |
| export SECURITY_USERNAME  |                                                                                                  | admin                 |
| export SECURITY_PASSWORD  |                                                                                                  | {cryption}....        |
| export CRYPTION_KEY       |                                                                                                  | EXAMPLE               |


**Runtime Settings**

Variables | Notes | Examples
--------|---------| -------------
export MEMBER_HOST_NM|The member bind address used by clients|$HOSTNAME
export LOCATOR1  | The first locator variable used in a docker/kubernetes environment
export LOCATOR2  | The second locator variable used in a docker/kubernetes environment
export LOCATOR3  | The third locator variable used in a docker/kubernetes environment
export LOCATOR_HOST|The locator bind address|$MEMBER_HOST_NM


**Disk Stores/Directories**

See [https://geode.apache.org/docs/guide/113/managing/disk_storage/disk_store_configuration_params.html](https://geode.apache.org/docs/guide/113/managing/disk_storage/disk_store_configuration_params.html) for more details

Variables | Notes | Examples
--------|---------| -------------
export DISK_STORE_DIR|Directory where disk store data will be stored|DISKSTORES
export WORK_DIR|The runtime working directory |$RUNTIME_DIR/work
export PDX_MAX_OPLOG_SIZE_MB|The PDX initial disk size|512
export DATA_DISK_MAX_OPLOG_SIZE_MB|The data initial disk size|512
export GW_DISK_MAX_OPLOG_SIZE_MB|The gateway disk store initial disk size|512
export DISK_STORE_QUEUE_SIZE|The queue size prior to flushing|40
export DISK_AUTO_COMPACT|Flag if disk should be auto compress|true


**Log/Stats**

See [https://geode.apache.org/docs/guide/113/managing/logging/setting_up_logging.html](https://geode.apache.org/docs/guide/113/managing/logging/setting_up_logging.html)

| Variables                     | Notes                                               | Examples                       |
|-------------------------------|-----------------------------------------------------|--------------------------------|
| export ENABLE_TIME_STATISTICS | Never set to true in production                     | false                          |
| export LOG_LEVEL              | the log level (ex: config, error,warn,severe, fine) | config                         |
| export LOG_DISK_LIMIT_MB      |                                                     | 5                              |
| export LOG_FILE_LIMIT_MB      |                                                     | 1                              |
| export STAT_DISK_LIMIT_MB     |                                                     | 5                              |
| export STAT_FILE_LIMIT_MB     |                                                     | 5                              |
| export LOC_STATS_FILE         |                                                     | locator_"$MEMBER_HOST_NM".gfs  |
| export DN_STATS_FILE          |                                                     | datanode_"$MEMBER_HOST_NM".gfs |

**Memory**

| Variables                  | Notes                      | Examples  |
|----------------------------|----------------------------|-----------|
| export DATA_NODE_HEAP_SIZE | The data node heap size    | 500m      |
| export LOCATOR_HEAP_SIZE   | The locator node heap size | 200m      |
| export REDUNDANCY_ZONE     | The redundancy             | $HOSTNAME |


**Ports**

See [https://geode.apache.org/docs/guide/113/configuring/running/firewalls_ports.html](https://geode.apache.org/docs/guide/113/configuring/running/firewalls_ports.html)


| Variables                        | Notes                                                  | Examples    | 
|----------------------------------|--------------------------------------------------------|-------------| 
| export PULSE_HTTP_PORT           | Set the 0 to disable running pulse embedded in locator | 0           |
| export REST_HTTP_PORT            | The Rest HTTP port                                     | 18080       |
| export JMX_MANAGER_PORT          | The JMX port                                           | 11099       | 
| export LOC_MEMBERSHIP_PORT_RANGE | Locator membership                                     | 10901-10910 |
| export LOC_TCP_PORT              | Locator TCP membership port                            | 10001       |
| export LOCATOR_PORT              | Locator client port                                    | 10334       |
| export LOCATOR_NM                | Locator name                                           | locator     |
| export CS_PORT                   | Cache Server client port                               | 10100       |
| export CS_TCP_PORT               | Cache Server membership port                           | 10002       |
| export CS_MEMBERSHIP_PORT_RANGE  | Cache Server membership UDP port range                 | 10801-10810 |
| export PROMETHEUS_LOC_PORT       | Prometheus port for locator                            | 17001       |
| export PROMETHEUS_DATA_NODE_PORT | Prometheus port for data node                          | 17011       |


**Gateway Settings**

[GemFire multi site configuration support](https://geode.apache.org/docs/guide/113/topologies_and_comm/multi_site_configuration/chapter_overview.html)

| Variables                    | Notes                             | Examples      |
|------------------------------|-----------------------------------|---------------|
| export DISTRIBUTED_ID        | Cluster unique ID                 | 1             |
| export REMOTE_DISTRIBUTED_ID | Cluster unique ID                 | 2             |
| export CS_GW_RECIEVER_PORT   | Port to receive gateway traffic   | 15000-15010   |
| export REMOTE_LOCATOR_PORT   | The remote cluster locator's port | $LOCATOR_PORT |



**Misc**

| export CS_NM            | Notes | server                                  |
|-------------------------|-------|-----------------------------------------|
| export GEMFIRE_HOME     |       | $GEMFIRE_INSTALL_DIR/$GEMFIRE_FOLDER_NM |
| export MEMBER_STAT_FILE |       | "$MEMBER_HOST_NM"_stat.gfd              |

--------------------------------------------------------------------
# Security

This framework supports a plugin implementation of the GemFire security manager
interface. Both LDAP and file properties based implementations are available.
See the [Apache Geode Security extension project](https://github.com/vmwarepivotallabs/dataTx-geode-security-extensions) for the implementation details.

See also [Apache Geode authentication examples](https://geode.apache.org/docs/guide/113/managing/security/authentication_examples.html) for more information.

Set the Apache Geode cluster user credentials with the following properties in the setenv.

```shell script
export SECURITY_USERNAME=cluster
export SECURITY_PASSWORD=YOURPASSWORDHERE
```


Note it is recommended to use encrypted passwords (see encryptionPassword.sh script)

  export SECURITY_PASSWORD={cryption}cndnirPoK+LecJOcWhnXmg==


### Setting `CRYPTION_KEY` configuration

The encryption and decryption of user password is based on a encryption key.

You must set the CRYPTION_KEY environment variable or as a JVM system property on each started Geode member (locators and cache server).

	export CRYPTION_KEY=<MY.ENCRYPTION.KEY.HERE>

### Encryption Password

  Use the following sample command to encrypt a password. NOTE: CRYPTION_KEY variable must match the value set on the server.


  ./encryptPassword.sh <PASSWORD>


## File Properties Security Manager

To use file property based security set the Geode security property in the setenv.sh.


  export SECURITY_MANAGER=io.pivotal.dataTx.geode.security.UserSecurityManager

  ### Configured Users


  You can  set the `export SECURITY_USER_PROPERTIES=$PWD/config/users.properties`  property to set a file that contains the security users passwords/privileges.

  The following is an example to set this JVM property using gfsh --J option.


  The following is an example file content

  	# First user
  	gemfire.security-users.<userName1>=userEncryptedPassword,[privilege] [,privilege]*

  	# Second user
  	gemfire.security-users.<userName2>=userEncryptedPassword,[privilege] [,privilege]*


  The following is an example default setting for an **admin** user with the **ALL** privilege and password:admin with in encrypted format when  `CRYPTION_KEY=PIVOTAL`

  	gemfire.security-users.admin={cryption}.....,ALL



  ### Privilege

  The User privilege are based on the Geode ResourePermission (Resource:Operation).

  - ALL - admin level user access with no restrictions
  - CLUSTER - all cluster read, write and manage permissions
  - CLUSTER:READ - cluster read permission
  - CLUSTER:WRITE - cluster write permission
  - CLUSTER:MANAGE - cluster management permissions such as shutdown cluster and members
  - DATA - all data read, write and manage permissions
  - DATA:READ - data read permission
  - DATA:WRITE - data write permission
  - DATA:MANAGE - data managed permissions such as creating regions




## LDAP Security Manager


Use the following to enable LDAP security.

    export SECURITY_MANAGER=io.pivotal.dataTx.geode.security.ldap.LdapSecurityMgr


    export SECURITY_LDAP_SERVER_URL=ldap://host:389
    export SECURITY_LDAP_BASE_DN=...
    export SECURITY_LDAP_PROXY_DN=..
    export SECURITY_LDAP_PROXY_PASSWORD={cryption}..
    export SECURITY_LDAP_MEMBEROF_ATTRIBUTE=memberOf
    export SECURITY_LDAP_UID_ATTRIBUTE=...
    export SECURITY_LDAP_GROUP_ATTRIBUTE=CN
    export SECURITY_LDAP_CACHING_EXPIRATION_MS=0
    export SECURITY_USER_PROPERTIES=$PWD/config/ldapUsers.properties


You can also set security properties in config/ldapUsers.properties


EXAMPLE

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

        # Number of milliseconds to cache LDAP credential (set to 0 to default )
      	security-ldap-caching-expiration-ms=0

      	# User credentials used to join the Geode cluster
      	security-username=cluster
      	security-password={cryption}6rvSAHPquoSszq1SVlbnrw==


** LDAP SSL **


When using  both LDAPS and enables  Geode SSL components like JMX, you should set the LDAP_USE_SSL_CONFIG_FACTORY

to true in the setenv.

EXAMPLE

    export LDAP_USE_SSL_CONFIG_FACTORY=true


You will also need to set the following in the setenv, based on a separate keystore
to use to LDAP.

    export LDAP_SSL-KEYSTORE=...
    export LDAP-SSL-TRUSTSTORE=..
    export LDAP-SSL-KEYSTORE-PASSWORD=..
    export LDAP-SSL-TRUSTSTORE-PASSSWORD=..


**ACL Permissions**

Same as the file properties implementation.


The Access Control List (ACL) permissions the property file are based on the Geode ResourePermission (Resource:Operation). The format of the property are security-ldap-acl-user-${UID} or security-ldap-acl-group-${groupID}.

    The following are example ACLs permissions

    ALL - admin level user access with no restrictions
    CLUSTER - all cluster read, write and manage permissions
    CLUSTER:READ - cluster read permission
    CLUSTER:WRITE - cluster write permission
    CLUSTER:MANAGE - cluster management permissions such as shutdown cluster and members
    DATA - all data read, write and manage permissions
    DATA:READ - data read permission
    DATA:WRITE - data write permission
    DATA:MANAGE - data managed permissions such as creating regions
    READ - cluster or data read permissions
    WRITE - cluster or data write permissions

## Docker

Create a file setenv_docker.sh

Example

```shell script
#!/bin/bash
# Docker

export IS_CONTAINER=true

#Debugging (Turn off in non local environments)
export REMOTE_DEBUGGING=false



# User Settings
export GEM_USER=root

export SSH_IDENTITY=""


# General Settings

#export CACHE_XML_FILE=config/cache.xml
export CACHE_XML_FILE=

#SSL/Security Settings
#export CERT_PASSWORD=password
export SSL_ENABLED_COMPONENTS=gateway
export CERT_VALIDITY_DAYS=365
export SECURITY_USERNAME=admin
export SECURITY_PASSWORD={cryption}cndnirPoK+LecJOcWhnXmg==
export CRYPTION_KEY=PIVOTAL

# File based security Support
export SECURITY_MANAGER=io.pivotal.dataTx.geode.security.UserSecurityManager
#export SECURITY_MANAGER=
export SECURITY_USER_PROPERTIES=$PWD/config/users.properties

#export SECURITY_MANAGER=io.pivotal.dataTx.geode.security.ldap.LdapSecurityMgr

# LDAP support

#export SECURITY_LDAP_SERVER_URL=ldap://localhost:389
#export SECURITY_LDAP_BASE_DN=ou=system
#export SECURITY_LDAP_PROXY_DN=uid=admin,ou=system
#export SECURITY_LDAP_PROXY_PASSWORD=secret
#export SECURITY_LDAP_MEMBEROF_ATTRIBUTE=memberOf
#export SECURITY_LDAP_UID_ATTRIBUTE=UID
#export SECURITY_LDAP_GROUP_ATTRIBUTE=cn
#export SECURITY_LDAP_ACL_GROUP_
#export SECURITY_LDAP_ACL_USER_ADMIN=ALL
#export SECURITY_USER_PROPERTIES=$PWD/config/ldapUsers.properties
#export SECURITY_LDAP_CACHING_EXPIRATION_MS=60000

#Reporting
export REPORT_DIR=/tmp

#Installation Settings
export ROOT_DIR=/usr
export DOWNLOAD_DIR=$ROOT_DIR/download
export INSTALL_DIR=$ROOT_DIR
export RUNTIME_DIR=$ROOT_DIR/runtime
export JAVA_INSTALL=$INSTALL_DIR/java
export JAVA_FOLDER_NM=jdk1.8.0_181
#export JAVA_HOME=$JAVA_INSTALL/$JAVA_FOLDER_NM
export JAVA_HOME=/usr/lib/jvm/java-1.8-openjdk

#export GEMFIRE_INSTALL_DIR=$ROOT_DIR/gemfire
export GEMFIRE_INSTALL_DIR=/devtools/repositories/IMDG
export S3_ROOT=https://s3.us-east-2.amazonaws.com/geode-gemfire
export JDK_TAR_BALL=jdk-8u181-linux-x64.tar.gz
export GEM_TAR_BALL=pivotal-gemfire-9.5.1.tgz
#export GEMFIRE_FOLDER_NM=pivotal-gemfire-9.5.1
#export GEMFIRE_FOLDER_NM=pivotal-gemfire-9.6.0
#export GEMFIRE_FOLDER_NM=pivotal-gemfire-9.8.0
export GEMFIRE_FOLDER_NM=/

export SECURITY_DIR=$INSTALL_DIR/security

#Backup directory
export BACKUP_DIR=$SOFTWARE_DIR/backup

#SSL/Security Settings
export CERT_PASSWORD=CHANGEME
export CERT_VALIDITY_DAYS=3600


#Runtime Settings
export MEMBER_HOST_NM=$HOSTNAME
#export MEMBER_HOST_NM=`curl -s http://169.254.169.254/latest/meta-data/public-hostname`
export LOCATOR_HOST=$MEMBER_HOST_NM
export REMOTE_LOCATOR=" "

#Disk Stores/Directories
export DISK_STORE_DIR=DISKSTORES
export WORK_DIR=$RUNTIME_DIR/work
export PDX_MAX_OPLOG_SIZE_MB=512
export DATA_DISK_MAX_OPLOG_SIZE_MB=512
export GW_DISK_MAX_OPLOG_SIZE_MB=512
export DISK_STORE_QUEUE_SIZE=40
export DISK_AUTO_COMPACT=true


#Log/Stats
export ENABLE_TIME_STATISTICS=false
export LOG_LEVEL=config
export LOG_DISK_LIMIT_MB=5
export LOG_FILE_LIMIT_MB=1
export STAT_DISK_LIMIT_MB=5
export STAT_FILE_LIMIT_MB=5
export LOC_STATS_FILE=locator_"$MEMBER_HOST_NM".gfs
export DN_STATS_FILE=datanode_"$MEMBER_HOST_NM".gfs
export LOG4J2_XML_FILE=$PWD/config/log4j2.xml

#Memory
export DATA_NODE_HEAP_SIZE=500m
export YOUNG_GENERATION_SIZE=12m
export LOCATOR_HEAP_SIZE=200m
export REDUNDANCY_ZONE=$HOSTNAME


# Derived Settings
export GEMFIRE_HOME=/geode
export DISTRIBUTED_ID=1
export REMOTE_DISTRIBUTED_ID=2


# Remote Locators locator1[locator1Port],locator2[locator2Port],etc
export REMOTE_LOCATORS=localhost[10000]


export PULSE_HTTP_PORT=17070
#export PULSE_HTTP_PORT=0
export REST_HTTP_PORT=18080
export JMX_MANAGER_PORT=11099
export LOC_MEMBERSHIP_PORT_RANGE=10901-10910
export LOC_TCP_PORT=10001
export LOCATOR_PORT=10334
export LOCATOR_NM=locator

export CS_PORT=10100
export CS_TCP_PORT=10002
export CS_MEMBERSHIP_PORT_RANGE=10801-10810
export CS_NM=server
export CS_GW_RECIEVER_PORT=15000-15010
export CS_REMOTE_DEBUGGING_PORT=14000

export MEMBER_STAT_FILE="$MEMBER_HOST_NM"_stat.gfd
export REMOTE_LOCATOR_PORT=10000

```

Create docker network

```shell script
docker network create geode-network
```

### Locator Docker image

```shell script
docker build  -f ./Dockerfile_locator  -t datatx-geode-locator:latest .
```

```shell script
docker run --hostname=locator1 --env=LOCATOR1=locator1 -p10334:10334 -p 17070:17070 -p 11099:11099  --network  geode-network datatx-geode-locator:latest
```


### Data node Docker image

```shell script
docker build  -f ./Dockerfile_dataNode  -t datatx-geode-data-node:latest .
```

```shell script
docker run --env=LOCATOR1=locator1  --hostname=server1  -p10100:10100 --network geode-network datatx-geode-data-node:latest
```


### Connecting through Gfsh

Get Container

```shell script
docker ps
```

Get IP address
```shell script
docker inspect <CONTAINER-ID>
```

```shell script
docker run -it --network geode-network apachegeode/geode
```


```shell script
connect --locator=locator1[10334]
```

User/password is admin/admin



*To connect from your local machine*

Edit /etc/hosts on linux

```
YOUR-IP-ADDRESS locator1
YOUR-IP-ADDRESS server1
```

In gfsh

```shell script
connect --jmx-manager=locator1[11099]
```

### kubernetes

The following explains a simple NON-production version
of an Apache Geode cluster running in Kubernetes. This has been tested a KIND Kubernetes instance.



Load the Locator and Data Node Docker images

```shell script
kind load docker-image datatx-geode-locator:latest --name=kind
kind load docker-image datatx-geode-data-node:latest --name=kind
```

Deploy Apache Geode Cluster

```shell script
k apply -f cloud/k8/geode-k8.yaml
```


Get a Bash shell to connect to the cluster
```shell script
kubectl exec --stdin --tty geode-0 -- /bin/bash
```

Execute gfsh
```
gfsh
```
Connect with user admin/admin
```
connect --locator=geode-0[10334]
```

Create a test region
```
gfsh>create region --name=test --type=PARTITION
```

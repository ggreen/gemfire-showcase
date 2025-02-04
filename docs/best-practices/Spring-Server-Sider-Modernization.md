# Spring for GemFire

version 2.0
Server-Side Modernization Guide

## Background

This document contains server-side guidelines for upgrading systems that use Spring Boot for Tanzu GemFire or Spring Data for Tanzu GemFire from Version 1.x to 2.x.

Version 2.x of Spring Boot for Tanzu GemFire or Spring Data for Tanzu GemFire has removed Spring-based Server Configuration Annotations and utilities. Support for configuring and bootstrapping a Tanzu GemFire server using Spring is discontinued. Systems can still leverage Spring for your Tanzu GemFire client applications, servers must now be start using alternative methods such as gfsh or native GemFire Java APIs.

See the following docs for more details

https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/spring-boot-for-tanzu-gemfire/2-0/gf-sb-2-0/upgrading-1-x-to-2-x.html
Server Migration Guides

For each of the following Spring Features, the following table identifies whether the feature is for clients or servers. It also provides guidance notes on how to migrate any server-related components using gfsh and or Java.


Spring Feature
Client/ Server
Version SERVER 2.0


# Migration Guidance

## @EnableSsl


Client and Server
[gfsh]

gfsh -e  "start server --name="$MEMBER_HOST_NM"

--security-properties-file=$SECURITY_DIR/gfsecurity.properties

[Java]

```java
new ServerLauncher.Builder()
.set("ssl-enabled-components”,”server”).
.set("ssl-protocols”,”any”).
.set("ssl-ciphers”,ciphers”).
.set("ssl-keystore”,”/path/to/trusted.keystore””).
.set("ssl-keystore-password”,password”).
.set("ssl-truststore”,”/path/to/trusted.keystore”).
.set("ssl-truststore-password”,password)

```
….


See SSL docs


## @EnablePdx

Client and Server

[gfsh]

```shell
configure pdx --read-serialized=true --auto-serializable-classes=".*" --disk-store
```


[Java]

```java
new ServerLauncher.Builder()
.setMemberName(serverName)
.setServerPort(serverPort)
.set("locators",locators)
.setWorkingDirectory(workingDirectory)
.setPdxReadSerialized(readPdxSerialized)
.setPdxSerializer(pdxSerializer)
.build();

```

See pdx docs

## @ClientCacheApplication

Client

N/A - no client-side changes needed


## @CacheServerApplication

Server

[gfsh]

```shell
./gfsh -e "start server --name=server1 …
```

[Java]


```java
new ServerLauncher.Builder()
.setMemberName(serverName)
.setServerPort(serverPort)
.set("locators",locators)
.setWorkingDirectory(workingDirectory)
.setPdxReadSerialized(readPdxSerialized)
.setPdxSerializer(pdxSerializer)
.build();

```

see running a cluster docs

## @EnableStatistics

Client and Server

[gfsh]

```shell
start server … --statistic-archive-file=$MEMBER_STAT_FILE -J=-Dgemfire.enable-time-statistics=$ENABLE_TIME_STATISTICS --J=-D-gemfire.statistic-sampling-enabled=true --J=-Dgemfire.archive-disk-space-limit=$STAT_DISK_LIMIT_MB ​​--J=-Dgemfire.archive-file-size-limit=$STAT_FILE_LIMIT_MB
```



[Java]

```java
      var serverLauncher = new ServerLauncher.Builder()
                .setMemberName(serverName)
                .setServerPort(serverPort)
                .set("locators",locators)
                .set("statistic-sampling-enabled","true")
                .set("statistic-archive-file",statisticArchiveFile)
                .set("archive-disk-space-limit",archiveDiskSpaceLimit)
                .set("archive-file-size-limit",archiveFileSizeLimit)
             .setWorkingDirectory(workingDirectory)
                .setPdxReadSerialized(readPdxSerialized)
                .setPdxDiskStore(pdxDiskStore)
                .setPdxSerializer(pdxSerializer)
                .build();
```
 

See stats docs

## @EnableGemfireFunctions

Server

[gfsh]

```shell
deploy --jar=/tmp/demo-functions-0.0.1-SNAPSHOT.jar
```

[Java]

```java
       var function = new NoOpFunction();
        FunctionService.registerFunction(function);

```

See functions docs



## @EnableClusterConfiguration & @EnableCaching

Client

N/A - no client-side changings need


## PartitionRegionsFactoryBean

Server

[gfsh]

```shell
create region --name=Location --type=PARTITION --enable-statistics=true
```


[Java]

```java
      Region<String, Account>  region  =  (Region)cache.createRegionFactory(RegionShortcut.PARTITION)
                .create("Account");

```

See region docs

## ReplicationRegionFactoryBean

Server

[gfsh]

```shell
create region --name=Location --type=REPLICATE --enable-statistics=true
```


[Java]


```java
Region<String, Account>  region  =  (Region)cache.createRegionFactory(RegionShortcut.REPLICATE)
.create("Account_replicated");

```

See region docs

## DiskStoreFactoryBean

Client/Server

[gfsh]

```shell
create disk-store --name=name
```

[Java]

```java
cache.createDiskStoreFactory().
.create(diskStoreName);

```

See disk store docs

## EvictionAttributesFactoryBean

Server

[gfsh]

```shell
create region --name=myRegion --type=PARTITION --eviction-entry-count=512 \
--eviction-action=overflow-to-disk

```

[Java]


```java
var evictionAttributes = EvictionAttributes.createLRUEntryAttributes(maxRegionEntries,EvictionAction.LOCAL_DESTROY)


(Region)cache.createRegionFactory(RegionShortcut.PARTITION)
.setEvictionAttributes(evictionAttributes)
.create("accountWithEviction");
```


See eviction docs

## AsyncEventQueueFactoryBean & AsyncEventQueue

Server

[gfsh]

```shell
create async-event-queue --id="persistentAsyncQueue" --persistent=true
--disk-store="diskStoreA" --parallel=true --listener=MyAsyncEventListener
--listener-param=url#jdbc:db2:SAMPLE --listener-param=username#gfeadmin --listener-param=password#admin1

```

[Java]

```java
Cache cache = new CacheFactory().create();
AsyncEventQueueFactory factory = cache.createAsyncEventQueueFactory();
factory.setPersistent(true);
factory.setDiskStoreName("diskStoreA");
factory.setParallel(true);
AsyncEventListener listener = new MyAsyncEventListener();
AsyncEventQueue persistentAsyncQueue = factory.create("customerWB", listener);

```

See async docs

## Custom TTL

Customer Expiry

Server

[gfsh]

```shell
create region --name=region1 --type=REPLICATE --enable-statistics \
--entry-idle-time-expiration=60 --entry-idle-time-custom-expiry=com.company.mypackage.MyClass

```


[java]

```java
region  =  (Region)cache.createRegionFactory(RegionShortcut.PARTITION)
.setCustomEntryIdleTimeout(customExpiry)
.setCustomEntryTimeToLive(customExpiry)
.create();

```

See expiry docs


## GatewaySenderFactoryBean

Server

[gfsh]

```shell
gfsh>create gateway-sender --id="sender2" --parallel=true --remote-distributed-system-id="2"

gfsh>create gateway-sender --id="sender3" --parallel=true --remote-distributed-system-id="3"

```

[java]


```java
Cache cache = new CacheFactory().create();

// Configure and create the gateway sender
GatewaySenderFactory gateway = cache.createGatewaySenderFactory();
gateway.setParallel(true);
GatewaySender sender = gateway.create("sender2", "2");
sender.start();
```

See gateway docs
GatewayReceiverFactoryBean
Server
[gfsh]

```shell
gfsh>create gateway-receiver --start-port=1530 --end-port=1551 \
--hostname-for-senders=gateway1.mycompany.com
```

[java]


```java
// Create or obtain the cache
Cache cache = new CacheFactory().create();

// Configure and create the gateway receiver
GatewayReceiverFactory gateway = cache.createGatewayReceiverFactory();
gateway.setStartPort(1530);
gateway.setEndPort(1551);
gateway.setHostnameForSenders("gateway1.mycompany.com");
GatewayReceiver receiver = gateway.create();
```

## MembershipListenerAdapter

Client/ Server


```java
//also see UniversalMembershipListenerAdapter

ManagementService.getExistingManagementService(CacheFactory.getAnyInstance())
.addMembershipListener(memberListener);
```


## GemfireLockRegistry

Client/Server

[java]

```java
var service = DistributedLockService.create("serviceName",CacheFactory.getAnyInstance().getDistributedSystem());

// on region level
/* Lock a data entry */
HashMap lockedItemsMap = new HashMap();
...
String entryKey = ...
        if (!lockedItemsMap.containsKey(entryKey))
        {
Lock lock = this.currRegion.getDistributedLock(entryKey);
lock.lock();
lockedItemsMap.put(name, lock);
}
        ...
/* Unlock a data entry */
String entryKey = ...
        if (lockedItemsMap.containsKey(entryKey))
        {
Lock lock = (Lock) lockedItemsMap.remove(name);
lock.unlock();
}
```



Also locking docs

## IndexFactoryBean

Server

[gfsh]

```shell
gfsh> create index --name=myIndex --expression=status --region=/exampleRegion
gfsh> create index --name=myKeyIndex --expression=id --region=/exampleRegion --type=key
```

[java]

```java
QueryService qs = cache.getQueryService();
qs.createIndex("myIndex", "status", "/exampleRegion");
qs.createKeyIndex("myKeyIndex", "id", "/exampleRegion");

```

see index docs

## Cache Loader

Client/ Server

[gfsh]

```shell
deploy --jars=/var/data/lib/myLoader.jar
```

```shell
create region --name=r3 \
--cache-loader=com.example.appname.myCacheLoader{'URL':'jdbc:cloudscape:rmi:MyData'}
```

[Java]

```java
RegionFactory<String,Object> rf = cache.createRegionFactory(REPLICATE);
rf.setCacheLoader(new QuoteLoader());
quotes = rf.create("NASDAQ-Quotes");
```


See loader docs


## ClientCacheConfigurer

Client

No client-side changes required.


-----------------------------
# Class Loader Isolation and Spring

Note that preferred option is launching GemFire server processes with class loader isolation. This option provides the highest reliability and availability.
The following extensions require class isolation
Tanzu GemFire Session Management Extension
Tanzu GemFire Vector Database
Tanzu GemFire Search

These features are not available when Spring Boot controls the class loader.
Launching GemFire server processes with gfsh-enabled class loader isolation would be the easiest option to maintain server-side management.
This would make it easier to move to platform-managed GemFire instances in the future such as GemFire for Kubernetes (see https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire-on-kubernetes/2-4/gf-k8s-2-4/index.html)
Class loader isolation is also available to Java applications that use the gemfire boot jar if you still desire to embed GemFire in a Java app versus using the preferred gfsh approach.
See examples documented here https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire/10-1/gf/configuring-running-running_the_cacheserver.html

Ex:

```shell
java -classpath gemfire-bootstrap.jar com.vmware.gemfire.bootstrap.Main \
com.examples.ExampleServerApplication \
--automatic-module-classpath <classes:jar:...> \
[application arguments ...]
```





```java
//Sample code

public class ExampleServerApplication {
public static void main(final String[] args) {
final ServerLauncher serverLauncher = new ServerLauncher.Builder()
.setMemberName("server1")
.setServerPort(40405).set("jmx-manager", "true")
.set("jmx-manager-start", "true")
.set("log-file", "").build();

    serverLauncher.start();
    System.out.println("Cache server successfully started");
    System.out.println(args[0]);
}
}
```



The following DOES NOT use classloader isolation


```shell
java -jar my-spring-boot-app-will-not-use-classloader-isolation.jar    [application arguments ...]
```


Notes from the product team

“when custom code is deployed on the server (any eventing or functions) we cannot guarantee that there will not be conflicts in 3rd party libraries, potentially causing failures.”
“The 3rd party library conflict constraint will extend to the Bootstrapping with Spring.”
“Please be aware that the last supported version to bootstrap a server is GemFire 10.1 and Spring Boot 3.3.”
“Any versions after Boot 3.3 will now by 2.0 (which is client-only)”
“Spring Boot 3.3 OSS support will end 2025-05-23 and Commercial will be  2026-08-23”

For the last point on Spring Boot open source support, see the enterprise support extended dates here

https://spring.io/projects/spring-boot#support


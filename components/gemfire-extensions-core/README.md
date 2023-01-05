# GemFire Extension Core

This project contains an API for Java Virtual Machine based languages to access [Apache Geode](https://geode.apache.org/)/[GemFire](https://tanzu.vmware.com/gemfire). It builds on top of the core GemFire APIs. It provides a simple interface to connect and perform GemFire data access operations.

# GemFire Extension

See [components/gemfire-extensions-core](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-extensions-core)

**Simple Configuration**

This API is cloud-native friendly. Many of the configurations such as the GemFire LOCATORS connection host[port] can be set using environment variables and or JVM Properties. It requires no XML or Java property files to configure the GemFire connection.

**Easy Security**

It provides out of box security credential login support. The username and password can be set using an environment variable or JVM property. Password encrypted/decryption is also supported.
It  supports loading keystore/trustores from the application CLASSPATH for SSL/TLS communication with the GemFire cluster.


This implement is based on the legacy [Apache Geode/GemFire Enterprise Data Integration framework](https://github.com/nyla-solutions/gedi-geode) open source implementation.

## GemFireClient API Developer Guide

**Setup Environment Single locator**

    export LOCATOR_HOST=localhost
    export LOCATOR_PORT=10334

or for multiple locators

    export LOCATORS=host1[10334],host2[10334]

**Setup Environment Multiple locators**


Format: host[port]`(,host[port])*`

Sample:

	export LOCATORS=host1[port],host2[port],host2[port]

**Optional Settings**

With defaults

	export PDX_SERIALIZER_CLASS_NM=...   (default org.apache.geode.pdx.ReflectionBasedAutoSerializer)

	export POOL_PR_SINGLE_HOP_ENABLED=false
	export PDX_CLASS_PATTERN=.*
	export USE_CACHING_PROXY=false
	export PDX_READ_SERIALIZED=false


If authentication is enabled

	export SECURITY_USERNAME=user
	export SECURITY_PASSWORD=password


If you need to set PDX read serialize to true (default false).

    export PDX_READ_SERIALIZED=true

**Cloud Foundry/ Pivotal Cloud Cache (PCC) Friendly**

Pivotal Cloud Cache [PCC](https://docs.pivotal.io/p-cloud-cache/index.html) is [Pivotal](http://pivotal.io)'s [12-factor](https://12factor.net/) [backing service](https://12factor.net/backing-services) implementation of GemFire. GemFireClient.connect method supports automatically wiring the locators hosts, ports and security credential when the PCC service is binded a Cloud Foundry application that using this API.


See [https://docs.pivotal.io/p-cloud-cache/using-pcc.html#bind-service](https://docs.pivotal.io/p-cloud-cache/using-pcc.html#bind-service)

	cf bind-service [appNAme] [pcc-service]


**SSL key/trust store management**

If you need SSL keystore/truststores loading via CLASSPATH for
12 factor cloud native applications such as cloud foundry Spring Boot application
see the properties below.

	export SSL_KEYSTORE_PASSWORD=...
	export SSL_PROTOCOLS=TLSv1.2
	export SSL_TRUSTSTORE_PASSWORD=...
	export SSL_KEYSTORE_TYPE=jks
	export SSL_CIPHERS=TLS_RSA_WITH_AES_128_GCM_SHA256
	export SSL_ENABLED_COMPONENTS=gateway,server,locator,jmx
	export SSL_REQUIRE_AUTHENTICATION=true
	export SSL_TRUSTSTORE_CLASSPATH_FILE=truststore.jks
	export SSL_KEYSTORE_CLASSPATH_FILE=keystore.jks

-------------------------------------------------

**Get a Apache Geode/GemFire Connection**

	GemFireClient gemFireClient = GemFireClient.connect()

Get the Apache Geode/GemFire client cache

	ClientCache cache = gemFireClient.getClientCache();


**Get a Region**

	//Does not require a client.xml or pre-registration of the region on the client
	//But, the region must exist on the server
	Region<String,PdxInstance> region = gemFireClient.getRegion("Test"))

**Execute a Query**

    Collection<Object> collection = gemFireClient.select("select * from /myregion");

**Get a queue continuous query matches**

    	Queue<Object> queue = registerCq("myQueryName","select * from /myregion")
    
    	//get first record
    	Object object = queue.poll(); //non blocking
    
    	//blocking
    	BlockingQueue<Object> queue = client.registerCq("testCq", "select * from /test");
	Object take = queue.take();

**Register simple java.util.Consumer listeners for region puts/delete events**

		Consumer<EntryEvent<String, Object>> customer = e -> System.out.println("Put event"+e);
		client.registerAfterPut("testEventRegion", putConsumer);
		client.getRegion("testEventRegion");

## Convert Statistics to CSV file

The following will extract a single statistic type with the name "CachePerfStats"

 	`java  com.vmware.data.services.gemfire.operations.stats.GfStatsReader /stats/stats.gfs CachePerfStats   /Projects/stats/CachePerfStats.csv`


To export all statistics with a file name pattern `<name>.gfs.<type>.csv` in the same directory as the stat file.

 	`java  com.vmware.data.services.gemfire.operations.stats.GfStatsReader /Projects/analysis/DigitIT/stats/stats.gfs`


## GemFire Commercial Repository


See the following for instruction to down the GemFire artifacts.

[https://gemfire.docs.pivotal.io/gemfire/getting_started/installation/obtain_gemfire_maven.html](https://gemfire.docs.pivotal.io/gemfire/getting_started/installation/obtain_gemfire_maven.html) 

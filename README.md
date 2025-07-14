# GemFire Extension 


This project contains example APIs, applications, design patterns implementations, demos and documentation for enabling the use of [GemFire](https://tanzu.vmware.com/gemfire).


## Prerequisites


- Java 17
- Gradle  8.4 or higher
- vmware-gemfire-10.1.0 or higher


## Demos


| Demo                                                                              | Notes                                                                                                                                                  |
|-----------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| [WAN REPLICATION ACTIVE_ACTIVE](docs/demo/local/WAN_REPLICATION_ACTIVE_ACTIVE.md) | Showcases the GemFire WAN replications                                                                                                                 |
| [GemFire Management Console](docs/demo/local/monitoring)                          | This demo will setup 2 WAN replication connected GemFire clusters. The GemFire Manage Console will be used to showcase the fleet management abilities. |


# GemFire API



| Project                                                                                                                                               | Notes                                                                                                                                                      |
|-------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [components/gemfire-extensions-core](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-extensions-core)                       | GemFire client API wrapper                                                                                                                                 |
| [components/gemfire-extensions-spring-security](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-extensions-spring-security) | Spring Security implementation backed by GemFire                                                                                                           | 
| [components/gemfire-http-dotnet-api](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-http-dotnet-api)                       | Wrapper Dotnet Core client that uses the GemFire HTTP API                                                                                                  |





# Management Operations 


| Project                                                                                                                                               | Notes                                                                                                                                                      |
|-------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [components/gemfire-extensions-core](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-extensions-core)                       | GemFire client API wrapper                                                                                                                                 |
| [gemfire-perf-test](applications/gemfire-perf-test)                                                                                                   | GemFire Performance test Tool                                                                                                                              |
| [applications/gemfire-gc-log-analyzer](applications/gemfire-gc-log-analyzer)                                                                          | JVM Java Garbage Collection logs file Analyzing                                                                                                            |                                                                                                                                                           | GemFire Health Check Tool                                                                                                                                  |
| [gemfire-health-log-analyzer](applications/gemfire-health-log-analyzer)                                                                               | Analyze and summary information in GemFire log files                                                                                                       |                                                                                                                                                           | GemFire Health Check Tool                                                                                                                                  |
 | [applications/operations/gemfire-stats-to-csv](applications/operations/gemfire-stats-to-csv)                                                          | Saves useful GemFire statistics to a CSV file                                                                                                              | 
| [components/gemfire-extensions-spring-security](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-extensions-spring-security) | Spring Security implements backed by GemFire                                                                                                               | 
| [applications/gemfire-health-shell-app](https://github.com/ggreen/gemfire-extensions/tree/main/applications/gemfire-health-shell-app)                 | [Spring Shell](https://spring.io/projects/spring-shell) application to analyzer GemFire statistics                                                         |
| [components/gemfire-health-office](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-health-office)                           | Tools for analyzing GemFire statistics                                                                                                                     |
| [components/gemfire-security-managers](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-security-managers)                   | [GemFire security manager](https://tanzu.vmware.com/developer/data/gemfire/blog/security-manager-basics-authentication-and-authorization/) implementations |
| [deployments/scripts/gemfire-devOps-bash](deployment/scripts/gemfire-devOps-bash)                                                                     | GemFire cluster operations DevOps management scripts                                                                                                       |


The following are GemFire utility functions

| Functions                                                                             | Notes                                                                     |
|---------------------------------------------------------------------------------------|---------------------------------------------------------------------------|
| [components/gemfire-touch-function](components/gemfire-touch-function)                | GemFire function to synchronize region in WAN replicated GemFire clusters |
| [gemfire-clear-region-function](components/functions/gemfire-clear-region-function)   | Clear all entries delete in a partitioned/replicated region               |
| [gemfire-delete-region-function](components/functions/gemfire-delete-region-function) | Delete entries in a region based on a matching sql statement              |
| [gemfire-object-sizing-function](components/functions/gemfire-object-sizing-function) | Determine the top entries sizes in region for debugging unbalanced data   |
| [gemfire-search-function](components/functions/gemfire-search-function)               | Function to perform a Full text search using GemFire Search               |


This follow are example GemFire client applications


| Application                                                                                | Notes                                                                                   |
|--------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| [applications/examples/spring/spring-hystrix](applications/examples/spring/spring-hystrix) | using [Hystrix](https://github.com/Netflix/Hystrix) with a Spring based GemFire client. |
 | [applications/examples/spring/pdx-migration](applications/examples/spring/pdx-migration)   | example reading from a GemFire snapshot. |
| [applications/examples/spring/account-service](applications/examples/spring/account-service)                                           | Sample Spring REST API GemFire client application|

## Building Source Code

Set up Maven Repository user credentials
See https://gemfire.dev/quickstart/java/

```shell
export BROADCOM_MAVEN_USERNAME=$HARBOR_USER
export BROADCOM_GEMFIRE_MAVEN_PASSWORD=$HARBOR_PASSWORD
```

Building source code

```shell
gradle  build
```

# GemFire Best Practices and Documentation

The following are documented best practices based on GemFire lessons learned.

| Subject                                                                | Notes                                               |
|------------------------------------------------------------------------|-----------------------------------------------------|
| [Class loader isolation](docs/best-practices/ClassLoader-Isolation.md) | GemFire server-side modules class loader guidelines |  
| [GemFire on Kubernetes Demos](docs/demo/k8)                            | GemFire, apps and services on Kubernetes Demos      |
| [GemFire Demos](docs/demo/local)                                       | Demos for various features                          |

--------------------------------------
# Starting a GemFire Cluster Locally
Set the GEMFIRE_HOME environment to the installation directory of GemFire.

Example
```shell
export GEMFIRE_HOME=/Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-10.1.0
```

See local startup script 

```shell
./deployment/local/gemfire/start.sh
```

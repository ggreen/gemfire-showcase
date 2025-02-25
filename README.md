# GemFire Extension 


This project contains example APIs, applications, design patterns implementations and documentation for enabling the use of [GemFire](https://tanzu.vmware.com/gemfire).


## Prerequisites


- Java 17
- Gradle  8.4 or higher
- vmware-gemfire-10.1.0 or higher



# Sub Projects


| Project                                                                                                                                               | Notes                                                                                                                                                      |
|-------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [components/gemfire-extensions-core](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-extensions-core)                       | GemFire client API wrapper                                                                                                                                 |
 | [gemfire-perf-test](applications/gemfire-perf-test)                                                                                                   | GemFire Performance test Tool                                                                                                                              |
| [components/gemfire-extensions-spring-security](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-extensions-spring-security) | Spring Security implements backed by GemFire                                                                                                               | 
| [applications/gemfire-health-shell-app](https://github.com/ggreen/gemfire-extensions/tree/main/applications/gemfire-health-shell-app)                 | [Spring Shell](https://spring.io/projects/spring-shell) application to analyzer GemFire statistics                                                         |
| [components/gemfire-health-office](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-health-office)                           | Tools for analyzing GemFire statistics                                                                                                                     |
| [components/gemfire-http-dotnet-api](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-http-dotnet-api)                       | Wrapper Dotnet Core client that uses the GemFire HTTP API                                                                                                  |
| [components/gemfire-security-managers](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-security-managers)                   | [GemFire security manager](https://tanzu.vmware.com/developer/data/gemfire/blog/security-manager-basics-authentication-and-authorization/) implementations |
| [components/gemfire-touch-function](components/gemfire-touch-function)                                                                                | GemFire function to synchronize region in WAN replicated GemFire clusters                                                                                  |
| [components/functions/gemfire-clear-region-function](components/functions/gemfire-clear-region-function)                                              | GemFire function to clear region data in a GemFire partitioned region                                                                                      |
| [deployments/scripts/gemfire-devOps-bash](deployment/scripts/gemfire-devOps-bash)                                                                     | GemFire cluster operations DevOps management scripts                                                                                                       |


## Building Source Code

Set up Maven Repository user credentials
See https://gemfire.dev/quickstart/java/

```shell
export BROADCOM_MAVEN_USERNAME=$HARBOR_USER
export BROADCOM_MAVEN_PASSWORD=$HARBOR_PASSWORD
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

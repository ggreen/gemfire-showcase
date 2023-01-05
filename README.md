# GemFire Extension 


This project contains an API for Java Virtual Machine based languages to access [Apache Geode](https://geode.apache.org/)/[GemFire](https://tanzu.vmware.com/gemfire). It builds on top of the core GemFire APIs. It provides a simple interface to connect and perform GemFire data access operations.


See [components/gemfire-extensions-core](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-extensions-core)


# Sub Projects


Project                   |  Notes
------------------------- | -------------------------------
[components/gemfire-extensions-core](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-extensions-core) | GemFire client API wrapper
[components/gemfire-extensions-spring-security](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-extensions-spring-security) | Spring Security implements backed by GemFire 
[applications/gemfire-health-shell-app](https://github.com/ggreen/gemfire-extensions/tree/main/applications/gemfire-health-shell-app) | [Spring Shell](https://spring.io/projects/spring-shell) application to analyzer GemFire statistics
[components/gemfire-health-office](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-health-office) | Tools for analyzing GemFire statistics
[components/gemfire-http-dotnet-api](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-http-dotnet-api) | Wrapper Dotnet Core client that uses the GemFire HTTP API
[components/gemfire-security-managers](https://github.com/ggreen/gemfire-extensions/tree/main/components/gemfire-security-managers) | [GemFire security manager](https://tanzu.vmware.com/developer/data/gemfire/blog/security-manager-basics-authentication-and-authorization/) implementations
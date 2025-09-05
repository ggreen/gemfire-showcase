

Pre-requisite

- Mac/Linux
- [Podman Desktop](https://podman-desktop.io/) 1.20 or higher
- [Java 17](https://formulae.brew.sh/formula/openjdk@17) -  brew install openjdk@17
- Apache Maven 3.9.1 + (ex: brew install maven@3.9)
- [Curl](https://formulae.brew.sh/formula/curl)  (Ex: brew install curl)
- [Wget](https://formulae.brew.sh/formula/wget) (ex: brew install wget)


Pull Docker images

```shell
podman pull gemfire/gemfire-management-console:1.4
podman pull gemfire/gemfire:10.0.3
podman pull gemfire/gemfire:10.1-jdk21
podman pull cloudnativedata/spring-gateway-healthcheck:0.0.2-SNAPSHOT
podman pull cloudnativedata/account-service-gemfire-showcase:0.0.1-SNAPSHOT
```
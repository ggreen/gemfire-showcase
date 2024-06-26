# Start GemFire

```shell
start locator --name=locator
```

```shell
configure pdx --read-serialized=true --disk-store
```

```shell
start server --name=server
```

```shell
create region --name=Account --type=PARTITION
```


# Building

export MAVEN_OPTS="-Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true"


```shell
mvn package
```


# Docker building image

```shell
cd examples/spring-data-gemfire/applications/account-service
mvn package

docker build   --platform linux/amd64,linux/arm64 -t account-service-gemfire-showcase:0.0.1-SNAPSHOT .
#docker build   --platform linux/amd64,linux/arm64 --build-arg JAR_FILE=build/libs/\*.jar -t account-service:0.0.1-SNAPSHOT .
#docker build  --platform linux/amd64,linux/arm64 -t account-service-gemfire-showcase:0.0.1-SNAPSHOT .

```

```shell
docker tag account-service-gemfire-showcase:0.0.1-SNAPSHOT cloudnativedata/account-service-gemfire-showcase:0.0.1-SNAPSHOT
docker push cloudnativedata/account-service-gemfire-showcase:0.0.1-SNAPSHOT
```

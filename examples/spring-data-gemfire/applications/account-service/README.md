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


# Docker

```shell script
docker tag account-rest-service:0.0.1-SNAPSHOT nyla/account-rest-service:0.0.1-SNAPSHOT 
docker login
docker push nyla/account-rest-service:0.0.1-SNAPSHOT

```
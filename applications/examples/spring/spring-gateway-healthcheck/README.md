
# Docker building image

```shell
cd examples/spring-gateway-healthcheck
gradle build

docker build   --platform linux/amd64,linux/arm64 -t spring-gateway-healthcheck:0.0.2-SNAPSHOT .
```

```shell
docker tag spring-gateway-healthcheck:0.0.2-SNAPSHOT cloudnativedata/spring-gateway-healthcheck:0.0.2-SNAPSHOT
docker push cloudnativedata/spring-gateway-healthcheck:0.0.2-SNAPSHOT
```

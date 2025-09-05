
# Docker building image

```shell
cd applications/examples/spring/spring-gateway-healthcheck
gradle build

docker build   --platform linux/amd64,linux/arm64 -t spring-gateway-healthcheck:0.0.3-SNAPSHOT .
```

```shell
docker tag spring-gateway-healthcheck:0.0.3-SNAPSHOT cloudnativedata/spring-gateway-healthcheck:0.0.3-SNAPSHOT
docker push cloudnativedata/spring-gateway-healthcheck:0.0.3-SNAPSHOT
```

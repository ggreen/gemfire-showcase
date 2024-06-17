
# Docker building image

```shell
cd examples/spring-gateway-healthcheck
gradle build

docker build   --platform linux/amd64,linux/arm64 -t spring-gateway-healthcheck:0.0.1-SNAPSHOT .
#docker build   --platform linux/amd64,linux/arm64 --build-arg JAR_FILE=build/libs/\*.jar -t account-service:0.0.1-SNAPSHOT .
#docker build  --platform linux/amd64,linux/arm64 -t spring-gateway-healthcheck:0.0.1-SNAPSHOT .

```

```shell
docker tag spring-gateway-healthcheck:0.0.1-SNAPSHOT cloudnativedata/spring-gateway-healthcheck:0.0.1-SNAPSHOT
docker push cloudnativedata/spring-gateway-healthcheck:0.0.1-SNAPSHOT
```

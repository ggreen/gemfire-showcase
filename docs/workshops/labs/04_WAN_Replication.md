
Start 2 WAN replication Clusters

```shell
./deployment/scripts/podman/labs/start-multi-clusters.sh
```


Start GMC

```shell
./deployment/scripts/podman/start-gmc-gideon-console.sh
```

Open GMC

```shell
open http://localhost:8080
```


## Start Data Services

Start application that points to cluster 1

```shell
podman run -it --rm --name=account-service-1  --network=gemfire-cache -p 8181:8181 cloudnativedata/account-service-gemfire-showcase:0.0.1-SNAPSHOT --gemfire.health.region.name=health1  --server.port=8181 --spring.data.gemfire.pool.default.locators="gf1-locator[10001]"
```




Start application that points to cluster 2

```shell
podman run -it --rm --name=account-service-2 --network=gemfire-cache -p 8282:8282 cloudnativedata/account-service-gemfire-showcase:0.0.1-SNAPSHOT --gemfire.health.region.name=health1  --server.port=8282 --spring.data.gemfire.pool.default.locators="gf2-locator[10002]" 
```

Start Spring Gateway

```shell
podman run -it --rm --name=spring-gateway --network=gemfire-cache -p 8011:8011 cloudnativedata/spring-gateway-healthcheck:0.0.3-SNAPSHOT --spring.gateway.fallback.enabled=true --gateway.fallback.httpUrl="http://account-service-2"  --SERVER_URI_1="http://account-service-1:8181" --gateway.fallback.port=8282  --server.port=8011
```


---------------
# Testing


Save userAccount 1

```shell
curl -X 'POST' \
  'http://localhost:8011/accounts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id": "1",
  "name": "account 1"
}'
```

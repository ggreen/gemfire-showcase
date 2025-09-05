
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
podman run -it --rm --name=account-service-2 --network=gemfire-cache -p 8282:8282 cloudnativedata/account-service-gemfire-showcase:0.0.1-SNAPSHOT --gemfire.health.region.name=health2  --server.port=8282 --spring.data.gemfire.pool.default.locators="gf2-locator[10002]" 
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


Get Account 1

```shell
curl http://localhost:8011/accounts/1
```


Save userAccount 2

```shell
curl 'http://localhost:8011/accounts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id": "2",
  "name": "account 2"
}'
```


Get Account 2

```shell
curl -X 'GET' \
  'http://localhost:8011/accounts/2' \
  -H 'accept: */*'
```

Test Actuator for App Service to cluster 1 expected "UP"
```shell
curl http://localhost:8181/actuator/health
```

Test Actuator for App Service to cluster 2 expected "UP"

```shell
curl http://localhost:8282/actuator/health
```

Stop Cluster 1 Servers

```shell
podman exec -it gf1-locator gfsh -e "connect --locator=gf1-locator[10001]" -e "shutdown"
```

Check app service to cluster 1 expected "DOWN"
```shell
curl http://localhost:8181/actuator/health
```

Test Actuator for App Service to cluster 2 expected "UP"
```shell
curl http://localhost:8282/actuator/health
```

Get Account 1
```shell
curl http://localhost:8011/accounts/1
```


Get Account 2
```shell
curl http://localhost:8011/accounts/2
```

Server 1

```shell
podman run -d -e 'ACCEPT_TERMS=y' --rm --name gf1-server --network=gemfire-cache gemfire/gemfire:10.1-jdk21 gfsh start server --name=gf1-server --use-cluster-configuration=true --server-port=10101   --locators="gf1-locator[10001]" --max-heap=1g   --initial-heap=1g --J=-Dgemfire.distributed-system-id=1 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777  --J=-Duser.timezone=America/New_York --J=-Dgemfire.prometheus.metrics.interval=15s
```

Sync data in cluster 2 to 1

```shell
podman exec -it gf1-locator gfsh -e "connect --locator=gf2-locator[10002]" -e "wan-copy region --region=/Account --sender-id=Account_Sender_to_1"
```

Get Account 1
```shell
curl http://localhost:8011/accounts/1
```

Get Account 2
```shell
curl http://localhost:8011/accounts/2
```

Shutdown cluster 2

```shell
podman exec -it gf1-locator gfsh -e "connect --locator=gf2-locator[10002]" -e "shutdown --include-locators"
```
Test Actuator for App Service to cluster 1 expected "UP"
```shell
curl http://localhost:8181/actuator/health
```

Test Actuator for App Service to cluster 2 expected Internal server error (locator is down)

```shell
curl http://localhost:8282/actuator/health
```

Get Account 1
```shell
curl http://localhost:8011/accounts/1
```

Get Account 2
```shell
curl http://localhost:8011/accounts/2
```

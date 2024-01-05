# GemFire Fail-Over Best Practices

The following architecture has been generally been viewed as a more reliable circuit breaker implementation for applications, then this demo application.

In this case, the client can use a standard circuit breaker pattern implementation such as Resilience4j or Hystrix. Failure detection can delegate to an App connected to another GemFire cluster (cluster A to B).

This architecture would also need ACTIVE to ACTIVE WAN replication between the clusters.

![img.png](img.png)


# GemFire Fail-over Demonstration

Start Clusters

```shell
./deployments/scripts/multi-cluster/multi-cluster-start.sh
```


Start 


```shell
java -jar examples/spring-data-gemfire/applications/account-service/build/libs/account-service-0.0.1-SNAPSHOT.jar --server.port=8181 --spring.data.gemfire.pool.default.locators="localhost[10001]"
```

```shell
java -jar examples/spring-data-gemfire/applications/account-service/build/libs/account-service-0.0.1-SNAPSHOT.jar  --server.port=8282 --spring.data.gemfire.pool.default.locators="localhost[10002]"
```



---------------

```shell
curl -X 'POST' \
  'http://localhost:8080/accounts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id": "2",
  "name": "2"
}'
```


```shell
curl -X 'GET' \
  'http://localhost:8080/accounts/2' \
  -H 'accept: */*'
```
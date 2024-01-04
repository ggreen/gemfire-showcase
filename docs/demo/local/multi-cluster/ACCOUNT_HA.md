

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
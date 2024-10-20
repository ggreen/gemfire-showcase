

```shell
./deployment/scripts/gideon-console/start-clusters.sh
```

```shell
./deployment/scripts/gideon-console/start-console.sh
```

gf1 

```yaml
host: host.docker.internal
port: 7071
```

gf2

```yaml
host: host.docker.internal
port: 7072
```



```shell
java -Xmx1g -Xms1g  -Daction=putString -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.2-SNAPSHOT.jar --action=putString --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=1000000 --startKeyValue=1 --endKeyValue=25000000 --batchSize=10 --valueSize=5 --spring.data.gemfire.pool.locators="localhost[10334]" --spring.data.gemfire.security.username=admin --spring.data.gemfire.security.password=admin --server.port=0
```
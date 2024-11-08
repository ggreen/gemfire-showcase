

```shell
./deployment/scripts/gideon-console/start-clusters.sh
```

```shell
./deployment/scripts/gideon-console/start-console.sh
```

gf1-cluster-1 

```yaml
host: host.docker.internal
port: 7071
```

gf2-cluster-2

```yaml
host: host.docker.internal
port: 7072
```

# Account Service

```shell
java -jar applications/examples/spring/account-service/build/libs/account-service-0.0.1-SNAPSHOT.jar --spring.data.gemfire.pool.locators="localhost[10334]" --server.port=8050
```

```shell
open http://localhost:8050
```


## Load Data

```shell
for i in {1..300}
do
  accountJson='{ "id": "';
  accountJson+=$i;
  accountJson+='", "name": "Account ';
  accountJson+=$i;
  accountJson+='"}';

  curl -X 'POST' \
  'http://localhost:8050/accounts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d $accountJson
  echo;  
done
```

Get Record

```shell
curl -X 'GET' \
  'http://localhost:8050/accounts/1' \
  -H 'accept: */*'
```
# Deploy Jars

Deploy Clear Function

```shell
/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase/components/functions/gemfire-clear-region-function/build/libs
```


## Gfsh Commands

```shell
list clients
```


# Performance Testing


putAndGetAndQuery

```shell
java -Xmx1g -Xms1g  -Daction=putString -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.2-SNAPSHOT.jar --action=putString --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=1000000 --startKeyValue=1 --endKeyValue=25000000 --batchSize=10 --valueSize=5 --spring.data.gemfire.pool.locators="localhost[10334]" --spring.data.gemfire.security.username=admin --spring.data.gemfire.security.password=admin --server.port=0
```

Load Testing

```shell
java -Xmx1g -Xms1g -jar -Daction=putAllString applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.2-SNAPSHOT.jar  --regionName="test"  --threadCount=5  --threadSleepMs=1000  --loopCount=1000 --batchSize=100 --keyPadLength=10 --valueLength=500 --seedText=T1 --server.port=0
```
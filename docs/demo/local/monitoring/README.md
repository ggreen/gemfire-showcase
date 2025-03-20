# Demo on GemFire management console

```shell
./deployment/scripts/gideon-console/docker/start-docker-gemfire.sh
```

```shell
./deployment/scripts/gideon-console/docker/start-gmc-gideon-console.sh
```

Open GemFire Management Console

```shell
open http://localhost:8080
```





Add Cluster 1
 

```properties
name=gf1-cluster-1
host=gf1-gl-locator
port=7071
```


Add Cluster 2


```properties
name=gf2-cluster-2
host=gf2-gl-locator
port=7072
```



# Account Service

Start Application

```shell
docker run -it --rm --name account-service-gemfire-showcase --network=gemfire-cache  -p 8050:8050  cloudnativedata/account-service-gemfire-showcase:0.0.1-SNAPSHOT --spring.data.gemfire.pool.locators="gf1-gl-locator[10334]" --server.port=8050
```
Open application UI


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

putString

```shell

docker run -it --rm  --name=gemfire-perf-test --network=gemfire-cache cloudnativedata/gemfire-perf-test:0.0.2-SNAPSHOT --action=putString --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=1000000 --startKeyValue=1 --endKeyValue=25000000 --batchSize=10 --valueSize=5 --spring.data.gemfire.pool.locators="gf1-gl-locator[10334]" --spring.data.gemfire.security.username=admin --spring.data.gemfire.security.password=admin --server.port=0
```


putAndGetAndQuery

```shell
docker run -it --rm  --name=gemfire-perf-test --network=gemfire-cache -e JAVA_OPTS=" -Xmx1g -Xms1g" cloudnativedata/gemfire-perf-test:0.0.3-SNAPSHOT --action=putAndGetAndQuery --regionName=test  --batchSize=10 --keyPadLength=10 --seedText=TEST --queryByKey="select key from /test.entries where key = \$1" --valueLength=500 --startKeyValue=1 --spring.data.gemfire.pool.locators="gf1-gl-locator[10334]" --spring.data.gemfire.security.username=admin --spring.data.gemfire.security.password=admin --server.port=0


```



Stress Testing (Will cause out of memory errors)

```shell
docker run -it --rm  --name=gemfire-perf-test --network=gemfire-cache -e JAVA_OPTS=" -Xmx1g -Xms1g" cloudnativedata/gemfire-perf-test:0.0.3-SNAPSHOT  --action=putAllString applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.2-SNAPSHOT.jar  --regionName="test"  --threadCount=5  --threadSleepMs=1000  --loopCount=1000 --batchSize=100 --keyPadLength=10 --valueLength=500 --seedText=T1 --server.port=0
```


# Shutdown

```shell
docker rm -f gf1-gl-locator gf1-gl-server gf2-gl-locator gf2-gl-server gideon-console account-service-gemfire-showcase
```


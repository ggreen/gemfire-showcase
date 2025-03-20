# GemFire Perf Test


| action | notes | args | example |
|--------|-------|------|---------|
| 

# Local Setup

Gfsh start

```shell
start locator --name=locator  --locators=127.0.0.1[10334]  --max-heap=250m --initial-heap=250m --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1
configure gemFireJson --read-serialized=true --disk-store
start server --name=server1 --max-heap=32g  --locators=127.0.0.1[10334]  --initial-heap=10g  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1
```


```shell
create region --name=test --type=PARTITION
```
## Docker Setup


Remove previous image
```shell
docker rmi gemfire-perf-test:0.0.3-SNAPSHOT
```

Build new image
```shell
gradle clean build :applications:gemfire-perf-test:bootBuildImage
```




```shell script
cd applications/gemfire-perf-test
docker build   --platform linux/amd64,linux/arm64 -t gemfire-perf-test:0.0.3-SNAPSHOT .
docker tag gemfire-perf-test:0.0.3-SNAPSHOT cloudnativedata/gemfire-perf-test:0.0.3-SNAPSHOT 
docker login
docker push cloudnativedata/gemfire-perf-test:0.0.3-SNAPSHOT

```


# Running test

Security Properties
```properties
spring.data.gemfire.security.username=admin
spring.data.gemfire.security.password=secret
```

## Region Put Test



Put String 10 character string, 10000 times with the key is generated in the range of 1 to 20

```shell

java -Xmx1g -Xms1g  -Daction=putAndGetAndQuery -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.2-SNAPSHOT.jar --action=putString --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=1000000 --startKeyValue=1 --endKeyValue=25000000 --batchSize=10 --valueSize=5 --spring.data.gemfire.pool.locators="localhost[10334]" --spring.data.gemfire.security.username=admin --spring.data.gemfire.security.password=admin --server.port=0

```


```shell
cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/apache-gemfire-extensions
java -Xmx1g -Xms1g -jar -Daction=putAllString applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.2-SNAPSHOT.jar  --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=10000 --batchSize=10000 --keyPadLength=10 --valueLength=500 --seedText=T1  --server.port=0
```


Load Testing

```shell
java -Xmx1g -Xms1g -jar -Daction=putAllString applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.2-SNAPSHOT.jar  --regionName="test"  --threadCount=10  --threadSleepMs=1000  --loopCount=2147483647 --batchSize=10000 --keyPadLength=10 --valueLength=500 --seedText=T1 --server.port=0
```



Throughput putStringThroughput



```shell
java  -Xmx1g -Xms1g -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.2-SNAPSHOT.jar --action=putStringThroughput --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=25005000 --maxCountPerThread=25005000 --valueLength=5 --keyPrefix=T1
```
## Region Get Test


Region Get 10,000 times of an entry of the first data entry in the region
```shell
cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/apache-gemfire-extensions
java -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.2-SNAPSHOT.jar --action=get --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=10000
```


General test


```shell
java -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.2-SNAPSHOT.jar --action=putAndGetAndQuery --batchSize=10 --keyPadLength=10 --valueLength=10 --seedText=TEST --queryByKey='select key from /test.entries where key = $1' --loopCount=2147483647 --threadSleepMs=1000 --server.port=0
```


# Building Docker

```shell

gradle build
cd applications/gemfire-perf-test
docker build   --platform linux/amd64,linux/arm64 -t gemfire-perf-test:0.0.2-SNAPSHOT .
```

```shell
docker tag gemfire-perf-test:0.0.2-SNAPSHOT cloudnativedata/gemfire-perf-test:0.0.2-SNAPSHOT
docker push cloudnativedata/gemfire-perf-test:0.0.2-SNAPSHOT
```
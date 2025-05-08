# GemFire Perf Test

This project is a open-source perf test for GemFire.
It measures latency and throughput based on the GemFire Java client.


A GemFire cluster can be limited by a number of factors such as 
infrastructure (e.g. network bandwidth, configurations 
and topology to applications. This project's goal is demonstrated baseline performance of a server or a cluster .


## Common Properties

| Property                              | Notes                                                                      | Default Value |
|---------------------------------------|----------------------------------------------------------------------------|---------------|
| spring.application.name               | The GemFire connection client name                                         |               |
| spring.data.gemfire.security.username | GemFire authentication user                                                | admin         |
| spring.data.gemfire.security.password | GemFire authentication password                                            | admin         |
| threadCount                           | the number of thread to use to for the performance test action             |               |
| threadSleepMs                         | Number of the milliseconds to pause between test in a loop                 |               |
| rampUPSeconds                         | The number of seconds to pause when adding mutliple threads                |               |
| loopCount                             | The number of time to execute the performance test action                  |               |
| threadLifeTimeSeconds                 | The number of seconds to await for termination of threads after loop count |               | 
| action                                | The GemFire performance test strategy (ex: putAndGetAndQuery)              |


# action=putAndGetAndQuery

This action performs the following operations
- Region Put 
- Region Get 
- OQL based Query


Example General test performance test

```shell
java -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.3-SNAPSHOT.jar --action=putAndGetAndQuery --batchSize=10 --keyPadLength=10 --valueLength=10 --seedText=TEST --queryByKey='select key from /test.entries where key = $1' --loopCount=1000 --threadSleepMs=1000 --server.port=0
```

The following are the action specific properties

| Property                              | Notes                                                                                           | Default Value |
|---------------------------------------|-------------------------------------------------------------------------------------------------|---------------|
| regionName                            | The GemFire server-side region used for the performance test                                    |               |
| batchSize                             | The number of entries to use for bulk operations such as Put all                                |               |
| keyPadLength                          | The fixed length size to use for generating random region entry keys                            |               |
| valueLength                           | The fixed length size to use for generating random region entry values                          |               |
| seedText                              | The fixed string to used within a general region entry key or value                             |
 | queryByKey                            | An GemFire query in OQL format for keys values Ex: select key from /test.entries where key = $1 |               | 



# action=get

This action performs region GET operations

Ex: Region Get 10,000 times of an entry of the first data entry in the region

```shell
java -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.3-SNAPSHOT.jar --action=get --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=10000
```

The following are the action specific properties

| Property                              | Notes                                                                                           | Default Value |
|---------------------------------------|-------------------------------------------------------------------------------------------------|---------------|
| regionName                            | The GemFire server-side region used for the performance test                                    |               |


# action=putAllString

This action performance testing region putall operations using strings

Example putall into the test region

```shell
java -Xmx1g -Xms1g -jar -Daction=putAllString applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.3-SNAPSHOT.jar  --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=10000 --batchSize=10000 --keyPadLength=10 --valueLength=500 --seedText=T1  --server.port=0
```

The following are the action specific properties

| Property                              | Notes                                                                                           | Default Value |
|---------------------------------------|-------------------------------------------------------------------------------------------------|---------------|
| regionName                            | The GemFire server-side region used for the performance test                                    |               |
| batchSize                             | The number of entries to use for bulk operations such as Put all                                |               |
| keyPadLength                          | The fixed length size to use for generating random region entry keys                            |               |
| valueLength                           | The fixed length size to use for generating random region entry values                          |               |
| seedText                              | The fixed string to used within a general region entry key or value                             |


# action=putString

This action will put a single entry string into a given region

Example:

```shell
java  -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.3-SNAPSHOT.jar --action=putString --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=1000000 --startKeyValue=1 --endKeyValue=25000000 --batchSize=10 --valueSize=5 --spring.data.gemfire.pool.locators="localhost[10334]" --spring.data.gemfire.security.username=admin --spring.data.gemfire.security.password=admin --server.port=0
```

The following are the action specific properties

| Property      | Notes                                                                | Default Value |
|---------------|----------------------------------------------------------------------|---------------|
| regionName    | The GemFire server-side region used for the performance test         |               |
| valueSize     | The number of character used to generated the entry string value     |               |
| startKeyValue | The minimum number to use for a single randomly generated region key |               |
| endKeyValue   | The maximum number to use for a single randomly generated region key |               |



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





Load Testing

```shell
java -Xmx1g -Xms1g -jar -Daction=putAllString applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.3-SNAPSHOT.jar  --regionName="test"  --threadCount=10  --threadSleepMs=1000  --loopCount=2147483647 --batchSize=10000 --keyPadLength=10 --valueLength=500 --seedText=T1 --server.port=0
```



Throughput putStringThroughput



```shell
java  -Xmx1g -Xms1g -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.3-SNAPSHOT.jar --action=putStringThroughput --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=25005000 --maxCountPerThread=25005000 --valueLength=5 --keyPrefix=T1
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
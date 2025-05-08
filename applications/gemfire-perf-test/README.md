# GemFire Perf Test

This project is a open-source perf test for GemFire.
It measures latency and throughput based on the GemFire Java client.


This application will print the following Example report to stand output:

```shell
report:
mean ms 0.054246415
min ms  0.04725
max ms  0.141291
70th ms 0.054041
90th ms 0.059458
99.9th ms       0.1315
99.999th ms     0.141291
stddev ms       0.008577032621179393

```

Note: A GemFire cluster can be limited by a number of factors such as 
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
java -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.3-SNAPSHOT.jar --action=putAndGetAndQuery --batchSize=10 --keyPadLength=10 --valueLength=10 --seedText=TEST --queryByKey='select key from /test.entries where key = $1' --loopCount=1000 --threadSleepMs=1 --server.port=0
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


# action=putStringThroughput


Performance generates a new key for get put operations.
Warning that this test can cause out of memory issues. 

Example 
```shell
java  -Xmx1g -Xms1g -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.3-SNAPSHOT.jar --action=putStringThroughput --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=10 --maxCountPerThread=10 --valueLength=5 --keyPrefix=T1
```

The following are the action specific properties

| Property          | Notes                                                                                             | Default Value |
|-------------------|---------------------------------------------------------------------------------------------------|---------------|
| regionName        | The GemFire server-side region used for the performance test                                      |               |
| valueLength       | The fixed length size to use for generating random region entry values                            |               |
| keyPrefix         | The string prefix to use when generating a key                                                    |               |
| maxCountPerThread | The maximum number of unique keys per thread to generate in order to prevent out of memory errors |               |



# Local Setup

See the example start script

```shell
./deployment/local/gemfire/start.sh
```


## Docker Setup


Build new image
```shell
gradle clean build :applications:gemfire-perf-test:bootBuildImage
```


The use the following to create multi-arch container images

```shell script
cd applications/gemfire-perf-test
docker build   --platform linux/amd64,linux/arm64 -t gemfire-perf-test:0.0.3-SNAPSHOT .
docker tag gemfire-perf-test:0.0.3-SNAPSHOT cloudnativedata/gemfire-perf-test:0.0.3-SNAPSHOT 
docker login
docker push cloudnativedata/gemfire-perf-test:0.0.3-SNAPSHOT

```



## Region Put Test











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
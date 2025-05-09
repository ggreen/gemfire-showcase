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

## Getting Started


Download

```shell
wget https://github.com/ggreen/gemfire-showcase/releases/download/gemfire-perf-test-0.0.3/gemfire-perf-test-0.0.3.jar
```

Example Jar

```shell
java -jar gemfire-perf-test-0.0.3.jar --action=get --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=10
```

Hit control/command ^C to stop

## Common Properties

| Property                              | Notes                                                                      | Default Value         |
|---------------------------------------|----------------------------------------------------------------------------|-----------------------|
| spring.data.gemfire.pool.locators     | List locators ex: HOST1[10334],HOST2[10334]                                | localhost[10334]      |
| spring.application.name               | The GemFire connection client name                                         |                       |
| spring.data.gemfire.security.username | GemFire authentication user                                                | admin                 |
| spring.data.gemfire.security.password | GemFire authentication password                                            | admin                 |
| threadCount                           | the number of thread to use to for the performance test action             | 10                    |
| threadSleepMs                         | Number of the milliseconds to pause between test in a loop                 | 0                     |
| rampUPSeconds                         | The number of seconds to pause when adding mutliple threads                | 1                     |
| loopCount                             | The number of time to execute the performance test action                  | 100000                |
| threadLifeTimeSeconds                 | The number of seconds to await for termination of threads after loop count | threadLifeTimeSeconds | 
| action                                | The GemFire performance test strategy (ex: putAndGetAndQuery)              |



# Performance Testing Actions

The performance test using a strategy pattern to perform a needed performance test.
This section explains the detail arguments for the various actions.


Supported Actions

| action              |                                                            |
|---------------------|------------------------------------------------------------|
 | putAndGetAndQuery   | Put, GET, and Query performance testing for a region       |
 | get                 | GET performance testing for a region                       | 
 | putAllString        | PutAll performance testing for a region                    |
 | putString           | Put of a string performance testing for a region           |
 | putStringThroughput | Put of a configured number of strings as a throughput test |

## action=putAndGetAndQuery

This action performs the following operations
- Region Put 
- Region Get 
- OQL based Query


Example General test performance test

```shell
java -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.3.jar --action=putAndGetAndQuery --batchSize=10 --keyPadLength=10 --valueLength=10 --seedText=TEST --queryByKey='select key from /test.entries where key = $1' --loopCount=1000 --threadSleepMs=1 --server.port=0
```


Running in docker


The following are the action specific properties

| Property     | Notes                                                                                           | Default Value |
|--------------|-------------------------------------------------------------------------------------------------|---------------|
| regionName   | The GemFire server-side region used for the performance test                                    | test          |
| batchSize    | The number of entries to use for bulk operations such as Put all                                |               |
| keyPadLength | The fixed length size to use for generating random region entry keys                            |               |
| valueLength  | The fixed length size to use for generating random region entry values                          |               |
| seedText     | The fixed string to used within a general region entry key or value                             |
 | queryByKey   | An GemFire query in OQL format for keys values Ex: select key from /test.entries where key = $1 |               | 



## action=get

This action performs region GET operations

Ex: Region Get 10,000 times of an entry of the first data entry in the region

```shell
java -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.3.jar --action=get --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=10000
```

The following are the action specific properties

| Property   | Notes                                                        | Default Value |
|------------|--------------------------------------------------------------|---------------|
| regionName | The GemFire server-side region used for the performance test | test          |


## action=putAllString

This action performance testing region putall operations using strings

Example putall into the test region

```shell
java -Xmx1g -Xms1g -jar -Daction=putAllString applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.3.jar  --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=10000 --batchSize=10000 --keyPadLength=10 --valueLength=500 --seedText=T1  --server.port=0
```



The following are the action specific properties

| Property     | Notes                                                                  | Default Value |
|--------------|------------------------------------------------------------------------|---------------|
| regionName   | The GemFire server-side region used for the performance test           | test          |
| batchSize    | The number of entries to use for bulk operations such as Put all       |               |
| keyPadLength | The fixed length size to use for generating random region entry keys   |               |
| valueLength  | The fixed length size to use for generating random region entry values |               |
| seedText     | The fixed string to used within a general region entry key or value    |


## action=putString

This action will put a single entry string into a given region

Example:

```shell
java  -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.3.jar --action=putString --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=1000000 --startKeyValue=1 --endKeyValue=25000000 --batchSize=10 --valueSize=5 --spring.data.gemfire.pool.locators="localhost[10334]" --spring.data.gemfire.security.username=admin --spring.data.gemfire.security.password=admin --server.port=0
```

The following are the action specific properties

| Property      | Notes                                                                | Default Value |
|---------------|----------------------------------------------------------------------|---------------|
| regionName    | The GemFire server-side region used for the performance test         | test          |
| valueSize     | The number of character used to generated the entry string value     | 10            |
| startKeyValue | The minimum number to use for a single randomly generated region key | 1             |
| endKeyValue   | The maximum number to use for a single randomly generated region key | 20            |


## action=putStringThroughput


Performance generates a new key for get put operations.
Warning that this test can cause out of memory issues. 

Example

```shell
java  -Xmx1g -Xms1g -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.3.jar --action=putStringThroughput --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=10 --maxCountPerThread=10 --valueLength=5 --keyPrefix=T1
```

The following are the action specific properties

| Property          | Notes                                                                                             | Default Value |
|-------------------|---------------------------------------------------------------------------------------------------|---------------|
| regionName        | The GemFire server-side region used for the performance test                                      | test          |
| valueLength       | The fixed length size to use for generating random region entry values                            |               |
| keyPrefix         | The string prefix to use when generating a key                                                    |               |
| maxCountPerThread | The maximum number of unique keys per thread to generate in order to prevent out of memory errors |               |


---------------------------------

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
gradle build
cd applications/gemfire-perf-test
docker build   --platform linux/amd64,linux/arm64 -t gemfire-perf-test:0.0.3 .
docker tag gemfire-perf-test:0.0.3 cloudnativedata/gemfire-perf-test:0.0.3 
docker login
docker push cloudnativedata/gemfire-perf-test:0.0.3

```




# Running in Podman


Start GemFire cluster in Podman

```shell
./deployment/scripts/podman/start-podman-perftest-gemfire.sh
```

Running Podman (replace podman with docker based on your container engineer)

```shell
podman run -it --network=gemfire-cache  cloudnativedata/gemfire-perf-test:0.0.3 -Xmx1g -Xms1g --action=putAllString --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=10 --batchSize=10 --keyPadLength=10 --valueLength=500 --seedText=T1  --server.port=0 --spring.data.gemfire.pool.locators="gf-locator[10334]"
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
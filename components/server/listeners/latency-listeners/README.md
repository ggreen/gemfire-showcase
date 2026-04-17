# latency-listeners


This function allows you to clear region data in a GemFire partitioned region.
This function has been tested with the following

- GemFire version 10.1.0 
- Java 17
- Gradle 8.4

## Build Jar

Set Maven Repository user credentials as environment variables.
See https://gemfire.dev/quickstart/java/

```shell
export BROADCOM_MAVEN_USERNAME=$HARBOR_USER
export BROADCOM_GEMFIRE_MAVEN_PASSWORD=$HARBOR_PASSWORD
```

Change directory to components from the root project directory

```shell
cd components/server/listeners/latency-listeners
```

Perform a Grade build

```shell
gradle build 
```

-------------------
# Testing

## Start GemFire

```shell
deployment/scripts/multi-cluster/start.sh
```

Connect gfsh 

```shell
$GEMFIRE_HOME/bin/gfsh
```
-----------------

# Delay Test

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "deploy --jar=$PWD/components/server/listeners/latency-listeners/build/libs/latency-listeners-0.0.1-SNAPSHOT-all.jar"
```

```shell
create region --name=delay-test --cache-writer=io.cloudNativeData.gemfire.latency.listeners.DelayCacherWriter --type=PARTITION
```


Test Delay

```shell
put --key=1 --value=1 --region=/delay-test
```
---------------------
Deploy jar to both clusters


```shell
$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "deploy --jar=$PWD/components/server/listeners/latency-listeners/build/libs/latency-listeners-0.0.1-SNAPSHOT-all.jar"


$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]" -e "deploy --jar=$PWD/components/server/listeners/latency-listeners/build/libs/latency-listeners-0.0.1-SNAPSHOT-all.jar"
```



```shell
$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "alter gateway-sender --id=Account_Sender_to_2 --gateway-event-filter=io.cloudNativeData.gemfire.latency.listeners.StartTimeLatencyGatewayEventFilter"
```

Add Cache Listener to cluster 2
```shell
$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]" -e "alter region --name=/Account --cache-listener=io.cloudNativeData.gemfire.latency.listeners.CsvWriterLatencyCacheListener"
```


Start Application connected to cluster 1

```shell
java -jar applications/examples/spring/account-service/build/libs/account-service-0.0.1-SNAPSHOT.jar --spring.data.gemfire.pool.locators="localhost[10001]" --server.port=8050
```


Add Data to cluster 1

```shell
curl -X 'POST' \
  'http://localhost:8080/region/Account/delete' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "0", "startTime": 0, "name": "Account", "@type": "AccountWithType"}
```


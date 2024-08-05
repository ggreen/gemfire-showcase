#  event-account-gemfire-sink

Example command to run the consumer

```shell
java -jar applications/event-account-gemfire-sink/target/event-account-gemfire-sink-0.0.2-SNAPSHOT.jar --spring.cloud.stream.bindings.input.destination=showcase.event.streaming.accounts --spring.cloud.stream.rabbit.bindings.input.consumer.containerType=stream --spring.cloud.stream.bindings.input.group=showcase.event.streaming.accounts --spring.cloud.stream.rabbit.bindings.input.consumer.queueNameGroupOnly=true --rabbitmq.streaming.offset=last 
```

## Start Gemfire

start locator in gfsh

```shell
start locator --name=locator1 --port=10334 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1
```

```shell
configure pdx --read-serialized=true --disk-store
```

start server
```shell
start server --name=server1 --locators=localhost[10334] --server-port=1880 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7778 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1 
```

Create Region
```shell
create region --name=/Account --type=PARTITION
```

---

# RabbitMQ CLI Tip

Review stream offsets
```shell
rabbitmq-streams -n rabbit stream_status showcase.event.streaming.accounts --tracking
```

# Docker building image

```shell
mvn install
cd applications/event-account-gemfire-sink
mvn package

docker build  --platform linux/amd64,linux/arm64 -t event-account-gemfire-sink:0.0.2-SNAPSHOT .

```

```shell
docker tag event-account-gemfire-sink:0.0.2-SNAPSHOT cloudnativedata/event-account-gemfire-sink:0.0.2-SNAPSHOT
docker push cloudnativedata/event-account-gemfire-sink:0.0.2-SNAPSHOT
```

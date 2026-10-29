# GemFire Sink

This Spring Boot microservice handles loading JSON payload to store 
as [GemFire](https://tanzu.vmware.com/gemfire) region entries.

The JSON input will be converted to an [PDX instance](https://gemfire.docs.pivotal.io/94/geode/developing/data_serialization/gemfire_pdx_serialization.html).


Example Start (from project root directory)

```shell script
java -jar applications/gemfire-sink/build/libs/gemfire-sink-0.0.2-SNAPSHOT.jar --regionName=TestGemFireSink --keyFieldExpression=id --valuePdxClassName=com.vmware.pivotallabs.dataTxt.domains.User
```


Property           | Notes                                      | Default
-----------------  | ------------------------------------------ | ----------------------
regionName         | The region to be loaded                    | 
keyFieldExpression | The JSON property to use as a key          | id
valuePdxClassName  | The full class name to store with entry    | java.lang.Object



```yaml
spring.cloud.stream.bindings.input.destination=myDestination
spring.cloud.stream.bindings.input.group=consumerGroup
#disable binder retries
spring.cloud.stream.bindings.input.consumer.max-attempts=1
spring.cloud.stream.rabbit.bindings.input.consumer.auto-bind-dlq=true
spring.cloud.stream.rabbit.bindings.input.consumer.dlq-ttl=5000
spring.cloud.stream.rabbit.bindings.input.consumer.dlq-dead-letter-exchange=
```

Example

```shell
--regionName=Account --spring.cloud.stream.bindings.input.group=testCdc --spring.cloud.stream.bindings.input.destination=testCdc  --spring.cloud.stream.bindings.input.group=testCdc --keyFieldExpression=key --server.port=7001
```


## Building Docker

```shell
mvn install
cd  applications/gemfire-sink
mvn spring-boot:build-image
cd ../..
```


```shell
docker tag gemfire-sink:0.0.1-SNAPSHOT cloudnativedata/gemfire-sink:0.0.1-SNAPSHOT
docker push cloudnativedata/gemfire-sink:0.0.1-SNAPSHOT
```

# RabbitMQ


spring.rabbitmq.stream.host: localhost
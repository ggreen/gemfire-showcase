# GemFire Sink

This Spring Boot microservice handles loading JSON payload to store 
as [GemFire](https://tanzu.vmware.com/gemfire) region entries.

The JSON input will be converted to an [PDX instance](https://gemfire.docs.pivotal.io/94/geode/developing/data_serialization/gemfire_pdx_serialization.html).


Example Start (from project root directory)

```shell script
java -jar applications/gemfire-sink-app/build/libs/gemfire-sink-app-0.0.1-SNAPSHOT.jar --regionName=TestGemFireSink --keyFieldExpression=id --valuePdxClassName=com.vmware.pivotallabs.dataTxt.domains.User --server.port=0
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
cd  applications/gemfire-sink-app
mvn spring-boot:build-image
cd ../..
```


```shell
docker tag gemfire-sink-app:0.0.1-SNAPSHOT cloudnativedata/gemfire-sink-app:0.0.1-SNAPSHOT
docker push cloudnativedata/gemfire-sink-app:0.0.1-SNAPSHOT
```

# RabbitMQ

Set the RabbitMq host
```properties
spring.rabbitmq.stream.host=localhost
spring.rabbitmq.stream.username=guest
spring.rabbitmq.stream.password=guest
spring.rabbitmq.stream.port=5552
spring.cloud.stream.gemFireSinkConsumer-in-0.destination=
spring.cloud.stream.gemFireSinkConsumer-in-0.group=
```

# Testing

Set property

```properties
content_type=application/json
```


Example Payload

```json
{
  "id": "0012",
  "@type" : "com.my.company.domain.Example"
}
```

```shell
gfsh>query --query="select * from /TestGemFireSink"
```


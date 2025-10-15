# RabbitMQ Async Listener Reference Implementation


This repository contains a reference implementation of an asynchronous listener for RabbitMQ. 
The GemFire is designed to public messages to a RabbitMQ exchange asynchronously, 
allowing for efficient processing of incoming messages.


##  Getting Started

Start GemFire 

```shell
./deployment/local/gemfire/start.sh 
```

Start RabbitMQ

```shell
podman run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4.1-management
```

Open RabbitMQ Management Console

user: guest
password: guest

```shell
open http://localhost:15672
```

Create quorum queue

```shell
podman exec -it  rabbitmq rabbitmqadmin declare queue name=listener.employees  queue_type=quorum 
```

Bind amq.topic to queue

```shell
podman exec -it  rabbitmq rabbitmqadmin declare binding source=amq.topic destination=gemfire.employees  routing_key="EMP.#"
```

Deploy Jar

```shell
$GEMFIRE_HOME/bin/gfsh -e connect  -e "deploy --jar=$PWD/components/gemfire-rabbitmq/build/libs/gemfire-rabbitmq-2.0.1-all.jar"
$GEMFIRE_HOME/bin/gfsh -e connect -e "create async-event-queue --id=employees --parallel=true --listener=gemfire.showcase.rabbitmq.listener.RabbitAsyncEventListener --listener-param=RABBIT_URIS=amqp://localhost:5672/"
$GEMFIRE_HOME/bin/gfsh -e connect  -e "create region --name=employees --type=PARTITION --async-event-queue-id=employees"
```


Put Data

```shell
$GEMFIRE_HOME/bin/gfsh -e connect  -e "put --key='EMP.1' --value='{\"id\":  \"EMP.$i\", \"name\": \"Jill Smith\"}' --region=employees"
```

```shell
for i in {1..10}; do
  $GEMFIRE_HOME/bin/gfsh -e connect  -e "put --key=EMP.$i --value='{\"id\": \"EMP.$i\", \"name\":, \"Customer $i\"}' --region=employees"
done
```

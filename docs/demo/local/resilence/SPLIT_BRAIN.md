
```shell
./deployment/scripts/resilence/start.sh
```

Start Applications

```shell
java -jar applications/examples/spring/account-service/build/libs/account-service-0.0.1-SNAPSHOT.jar --spring.data.gemfire.pool.locators="localhost[10334],localhost[10002]" --server.port=8050
```

```shell
for i in {1..4}
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

```shell
for i in {1..4}
do
    curl -X 'GET' \
      "http://localhost:8050/accounts/${i}" \
      -H 'accept: */*'
      echo; 
done
```

Cause a split brain

```shell
LOC1_PID=`ps -ef | grep java| grep rlocator1 | awk '{print $2}'`
echo $LOC1_PID
SRV1_PID=`ps -ef | grep java| grep rserver1 | awk '{print $2}'`
echo $SRV1_PID

kill -6 $LOC1_PID $SRV1_PID
````


```shell
for i in {1..4}
do
    curl -X 'GET' \
      "http://localhost:8050/accounts/${i}" \
      -H 'accept: */*'
      echo; 
done
```

Will not fail

add more


```shell
for i in {5..10}
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

```shell
for i in {5..10}
do
    curl -X 'GET' \
      "http://localhost:8050/accounts/${i}" \
      -H 'accept: */*'
      echo; 
done
```


Add back failed Locator

```shell
cd $GEMFIRE_HOME/bin
$GEMFIRE_HOME/bin/gfsh -e "start locator --name=rlocator1 --locators=localhost[10334],localhost[10002] --port=10334 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777 --J=-Dgemfire.enable-network-partition-detection=false --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1"
```

Add back failed server

```shell
cd $GEMFIRE_HOME/bin
$GEMFIRE_HOME/bin/gfsh -e "start server --name=rserver1 --locators=localhost[10334],localhost[10002] --server-port=1880 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7778  --J=-Dgemfire.enable-network-partition-detection=false --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1  --http-service-port=8590"
```


Get all data

add more


```shell
for i in {11..15}
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

```shell
for i in {1..15}
do
    curl -X 'GET' \
      "http://localhost:8050/accounts/${i}" \
      -H 'accept: */*'
      echo; 
done
```




Shutdown cluster

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]" -e "shutdown --include-locators"
```







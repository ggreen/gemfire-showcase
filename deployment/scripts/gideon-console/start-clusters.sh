cd $GEMFIRE_HOME/bin

./gfsh -e "start locator --name=gf1-gl-locator --port=10334 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1 --http-service-port=7071 --J=-Dgemfire.jmx-manager-port=1099 --max-heap=250m --initial-heap=250m --J=-Dgemfire.distributed-system-id=1 --J=-Dgemfire.remote-locators=localhost[10002]"

curl http://localhost:7777/metrics

./gfsh -e "connect" -e "configure pdx --read-serialized=true --disk-store"

./gfsh -e "start server --name=gf1-gl-server --locators=localhost[10334] --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7778 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1 --use-cluster-configuration=true --server-port=10101 --max-heap=1g   --initial-heap=1g --J=-Dgemfire.distributed-system-id=1"

curl http://localhost:7778/metrics

# host.docker.internal

$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10334]"  -e  "create gateway-receiver" -e  "create gateway-sender --id=Account_Sender_to_2 --parallel=true  --remote-distributed-system-id=2 --enable-persistence=true --enable-batch-conflation=true"

$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10334]" -e  "create gateway-sender --id=test_Sender_to_2 --parallel=true  --remote-distributed-system-id=2 --enable-persistence=true --enable-batch-conflation=true"

$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10334]"  -e  "create region --name=Account --type=PARTITION --gateway-sender-id=Account_Sender_to_2"


$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10334]"  -e  "create region --name=health1 --type=PARTITION"


$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10334]"  -e  "create region --name=test --type=PARTITION --gateway-sender-id=test_Sender_to_2"


# ------ Cluster 2 -------------------
$GEMFIRE_HOME/bin/gfsh -e "start locator --name=gf2-gl-locator --enable-cluster-configuration=true --connect=false  --http-service-port=7072 --J=-Dgemfire.tcp-port=11111 --J=-Dgemfire.jmx-manager-port=1098 --max-heap=250m --initial-heap=250m --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1  --J=-Dgemfire.distributed-system-id=2 --port=10002 --J=-Dgemfire.remote-locators=localhost[10334] --J=-Dgemfire.prometheus.metrics.port=9778 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --J=-Dgemfire.prometheus.metrics.emission=Default "

curl http://localhost:9778/metrics

$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]"  -e  "configure pdx --read-serialized=true --disk-store"

$GEMFIRE_HOME/bin/gfsh -e "start server --name=gf2-gl-server --use-cluster-configuration=true --server-port=10102   --locators=127.0.0.1[10002] --max-heap=1g   --initial-heap=1g  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1  --J=-Dgemfire.distributed-system-id=2 --J=-Dgemfire.prometheus.metrics.port=9779 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --J=-Dgemfire.prometheus.metrics.emission=Default"

$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]"  -e  "create gateway-receiver" -e  "create gateway-sender --id=Account_Sender_to_1 --parallel=true  --remote-distributed-system-id=1 --enable-persistence=true --enable-batch-conflation=true"


$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]" -e  "create gateway-sender --id=test_to_1 --parallel=true  --remote-distributed-system-id=1 --enable-persistence=true --enable-batch-conflation=true"

$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]"  -e  "create region --name=Account --type=PARTITION --gateway-sender-id=Account_Sender_to_1"

$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]"  -e  "create region --name=health2 --type=PARTITION"


$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]"  -e  "create region --name=test --type=PARTITION --gateway-sender-id=test_to_1"
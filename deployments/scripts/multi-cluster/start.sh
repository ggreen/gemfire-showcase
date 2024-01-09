#export GEMFIRE_HOME=/Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-10.0.2

# ------ Cluster 1 -------------------
$GEMFIRE_HOME/bin/gfsh -e "start locator --name=gf1-locator  --enable-cluster-configuration=true --connect=false --port=10001 --http-service-port=7071 --J=-Dgemfire.jmx-manager-port=1099 --max-heap=250m --initial-heap=250m --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1 --J=-Dgemfire.distributed-system-id=1 --J=-Dgemfire.remote-locators=localhost[10002]"


$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "configure pdx --read-serialized=true --disk-store"


$GEMFIRE_HOME/bin/gfsh -e "start server --name=gf1-server --use-cluster-configuration=true --server-port=10101   --locators=127.0.0.1[10001] --max-heap=1g   --initial-heap=1g  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1  --J=-Dgemfire.distributed-system-id=1"



$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10001]"  -e  "create gateway-receiver" -e  "create gateway-sender --id=Account_Sender_to_2 --parallel=true  --remote-distributed-system-id=2 --enable-persistence=true --enable-batch-conflation=true"

$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10001]"  -e  "create region --name=Account --type=PARTITION --gateway-sender-id=Account_Sender_to_2"
$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10001]"  -e  "create region --name=health1 --type=PARTITION"


# ------ Cluster 2 -------------------
$GEMFIRE_HOME/bin/gfsh -e "start locator --name=gf2-locator --enable-cluster-configuration=true --connect=false  --http-service-port=7072 --J=-Dgemfire.tcp-port=11111 --port=10002 --J=-Dgemfire.jmx-manager-port=1098 --max-heap=250m --initial-heap=250m --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1  --J=-Dgemfire.distributed-system-id=2 --J=-Dgemfire.remote-locators=localhost[10001]"

$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]"  -e  "configure pdx --read-serialized=true --disk-store"

$GEMFIRE_HOME/bin/gfsh -e "start server --name=gf2-server --use-cluster-configuration=true --server-port=10102   --locators=127.0.0.1[10002] --max-heap=1g   --initial-heap=1g  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1  --J=-Dgemfire.distributed-system-id=2"

$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]"  -e  "create gateway-receiver" -e  "create gateway-sender --id=Account_Sender_to_1 --parallel=true  --remote-distributed-system-id=1 --enable-persistence=true --enable-batch-conflation=true"

$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]"  -e  "create region --name=Account --type=PARTITION --gateway-sender-id=Account_Sender_to_1"
$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]"  -e  "create region --name=health2 --type=PARTITION"
mkdir -p $GF_DIR/gf-cluster/green

#Start Locator
cd $GF_DIR/gf-cluster/green
$GEMFIRE_GREEN_HOME/bin/gfsh -e "start locator --name=gf1-locator --J=-Dgemfire.distributed-system-id=2   --J=-Dgemfire.remote-locators=127.0.0.1[10001] --enable-cluster-configuration=true --connect=false  --http-service-port=0 --J=-Dgemfire.tcp-port=11111 --port=10002 --J=-Dgemfire.jmx-manager-port=1098 --max-heap=250m --initial-heap=250m --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1"
# Configure PDX
$GEMFIRE_GREEN_HOME/bin/gfsh -e "connect --locator=localhost[10002]" -e "configure pdx --read-serialized=true --disk-store"
# Server 1
cd $GF_DIR/gf-cluster/green
$GEMFIRE_GREEN_HOME/bin/gfsh -e "start server --name=gf1-server  --J=-Dgemfire.distributed-system-id=2   --use-cluster-configuration=true --server-port=10201   --locators=127.0.0.1[10002] --max-heap=1g   --initial-heap=1g  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1" &
# Server 2
cd $GF_DIR/gf-cluster/green
$GEMFIRE_GREEN_HOME/bin/gfsh -e "start server --name=gf2-server  --J=-Dgemfire.distributed-system-id=2   --use-cluster-configuration=true --server-port=10202   --locators=127.0.0.1[10002] --max-heap=1g   --initial-heap=1g  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1"

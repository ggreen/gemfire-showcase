export GEMFIRE_BLUE_HOME=/Users/devtools/repositories/IMDG/gemfire/archive/pivotal-gemfire-9.10.10
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_421.jdk/Contents/Home/
export PATH=$JAVA_HOME/bin:$PATH
export GF_DIR=/Users/devtools/repositories/IMDG/gemfire/runtime/zero-downtime-upgrade

#Setup directories

rm -rf mkdir -p $GF_DIR/gf-cluster
mkdir -p $GF_DIR/gf-cluster/blue
mkdir -p $GF_DIR/gf-cluster/green


## Start Blue Cluster


#Start Locator
# Locator
cd $GF_DIR/gf-cluster/blue
$GEMFIRE_BLUE_HOME/bin/gfsh -e "start locator --name=gf1-locator --enable-cluster-configuration=true --connect=false --port=10001  --J=-Dgemfire.jmx-manager-port=1099 --max-heap=250m --initial-heap=250m --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1"
$GEMFIRE_BLUE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "configure pdx --read-serialized=true --disk-store"

# Server 1
cd $GF_DIR/gf-cluster/blue
$GEMFIRE_BLUE_HOME/bin/gfsh -e "start server --name=gf1-server --use-cluster-configuration=true --server-port=10101   --locators=127.0.0.1[10001] --max-heap=1g   --initial-heap=1g  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1"

# Server 2
cd $GF_DIR/gf-cluster/blue
$GEMFIRE_BLUE_HOME/bin/gfsh -e "start server --name=gf2-server --use-cluster-configuration=true --server-port=10102   --locators=127.0.0.1[10001] --max-heap=1g   --initial-heap=1g  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1"

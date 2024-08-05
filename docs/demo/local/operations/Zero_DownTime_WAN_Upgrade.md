# Zero DownTime Upgrade


Blue Setup

Goto project root


Example
```shell
cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase
```

```shell
source deployment/upgrades/blue-setenv.sh
./deployment/upgrades/blue_start.sh
```


```shell
$GEMFIRE_BLUE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "list members"
```

Create Region

```shell
$GEMFIRE_BLUE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "create region --name=accounts --type=PARTITION_REDUNDANT_PERSISTENT"
```

Add data

```shell
$GEMFIRE_BLUE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "put --region=/accounts --key=1 --value="account-1""
```
-------------------------------------

## Start Green Cluster
In Gfsh (new Terminal Shell)

```shell
source deployment/upgrades/green-setenv.sh
```

```shell
./deployment/upgrades/green_start.sh
```

Gateway Receiver

```shell
$GEMFIRE_GREEN_HOME/bin/gfsh -e "connect --locator=localhost[10002]" -e "create gateway-receiver --start-port=7520 --end-port=7521"
```

Verify

```shell
$GEMFIRE_GREEN_HOME/bin/gfsh -e "connect --locator=localhost[10002]" -e "list members"
```

Create Region

```shell
$GEMFIRE_GREEN_HOME/bin/gfsh -e "connect --locator=localhost[10002]" -e "create region --name=accounts --type=PARTITION_REDUNDANT_PERSISTENT"
```

--------------------

Add Gateway to Blue Cluster


## Create Gateway receiver to cluster 1

```shell
$GEMFIRE_BLUE_HOME/bin/gfsh -e "connect --locator=[10001]" -e "create gateway-sender --id=cluster2 --parallel=true --remote-distributed-system-id=2 --enable-persistence=true"
```

```shell
$GEMFIRE_BLUE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "put --region=/accounts --key=1 --value="account-1-A""
```

```shell
$GEMFIRE_BLUE_HOME/bin/gfsh -e "connect --locator=[10001]" -e "alter region --name=/accounts  --gateway-sender-id=cluster2"
```

```shell
$GEMFIRE_BLUE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "put --region=/accounts --key=1 --value="account-1-B""
```

Stop Locator

```shell
$GEMFIRE_BLUE_HOME/bin/gfsh -e "connect --locator=[10001]" -e "stop locator --name=gf1-locator"
```

Start Locator to remote locators
```shell
cd $GF_DIR/gf-cluster/blue
$GEMFIRE_BLUE_HOME/bin/gfsh -e "start locator --name=gf1-locator --J=-Dgemfire.remote-locators=127.0.0.1[10002] --enable-cluster-configuration=true --connect=false --port=10001  --J=-Dgemfire.jmx-manager-port=1099 --max-heap=250m --initial-heap=250m --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1"
```

Verify members

```shell
$GEMFIRE_BLUE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "list members"
```


Restart Gateway Receiver

Green Cluster
```shell
$GEMFIRE_GREEN_HOME/bin/gfsh -e "connect --locator=localhost[10002]"  -e "stop gateway-receiver"
$GEMFIRE_GREEN_HOME/bin/gfsh -e "connect --locator=localhost[10002]"  -e "start gateway-receiver"
```

Blue Cluster
```shell
$GEMFIRE_BLUE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "stop gateway-sender --id=cluster2"
$GEMFIRE_BLUE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "start gateway-sender --id=cluster2"
```

Verify Connected Sender (restart receivers and senders as needed)

```shell
$GEMFIRE_GREEN_HOME/bin/gfsh -e "connect --locator=localhost[10002]"  -e "list gateways"
```


## Backup Old Cluster


Backup Blue Cluster

```shell
mkdir -p $GF_DIR/gf-cluster/blue/backup
```

```shell
$GEMFIRE_BLUE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "backup disk-store --dir=$GF_DIR/gf-cluster/blue/backup"
```


Stop Green Cluster

```shell
$GEMFIRE_GREEN_HOME/bin/gfsh -e "connect --locator=localhost[10002]" -e "shutdown --include-locators=true"
```


Blue Cluster -> Edit restore.sh to copy disk stores to green cluster
```shell
vi $GF_DIR/gf-cluster/blue/backup/*/*/restore.sh
```

Run modified restores.sh on ONLY the servers

```shell
cd backup/*/*gf1_server*/
./restore.sh
```


```shell
cd ../*gf2_server*/
./restore.sh
```

Example restore.sh

```shell


# Restore data
rm -rf '/Users/devtools/repositories/IMDG/gemfire/runtime/zero-downtime-upgrade/gf-cluster/green/gf1-server/'
mkdir -p '/Users/devtools/repositories/IMDG/gemfire/runtime/zero-downtime-upgrade/gf-cluster/green/gf1-server/.'
cp -rp 'diskstores/DEFAULT_95216c211a9444a5-8c10326d5c720b26/dir0'/* '/Users/devtools/repositories/IMDG/gemfire/runtime/zero-downtime-upgrade/gf-cluster/green/gf1-server/.'
```


-----

Restart Cluster Green

Start Locator
```shell
cd $GF_DIR/gf-cluster/green
$GEMFIRE_GREEN_HOME/bin/gfsh -e "start locator --name=gf1-locator --J=-Dgemfire.distributed-system-id=2   --J=-Dgemfire.remote-locators=127.0.0.1[10001] --enable-cluster-configuration=true --connect=false  --http-service-port=0 --J=-Dgemfire.tcp-port=11111 --port=10002 --J=-Dgemfire.jmx-manager-port=1098 --max-heap=250m --initial-heap=250m --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1"
```


Server 1
```shell
cd $GF_DIR/gf-cluster/green
$GEMFIRE_GREEN_HOME/bin/gfsh -e "start server --name=gf1-server  --J=-Dgemfire.distributed-system-id=2   --use-cluster-configuration=true --server-port=10201   --locators=127.0.0.1[10002] --max-heap=1g   --initial-heap=1g  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1" &
```

Server 2
```shell
cd $GF_DIR/gf-cluster/green
$GEMFIRE_GREEN_HOME/bin/gfsh -e "start server --name=gf2-server  --J=-Dgemfire.distributed-system-id=2   --use-cluster-configuration=true --server-port=10202   --locators=127.0.0.1[10002] --max-heap=1g   --initial-heap=1g  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1"
```



------------------------------------------
# WAN Replication - Parallel - Testing


Verify --value="account-1-B"

```shell
$GEMFIRE_GREEN_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "get --region=/accounts --key=1"
```



```shell
$GEMFIRE_BLUE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "put --region=/accounts --key=1 --value="account-1-C""
```


Verify value=account-1-C

```shell
$GEMFIRE_GREEN_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "get --region=/accounts --key=1"
```


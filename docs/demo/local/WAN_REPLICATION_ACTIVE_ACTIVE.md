# WAN Replication (Active-Active)

The following explains how to set up two [GemFire](https://www.vmware.com/products/gemfire.html) clusters connected by a 
[WAN replication](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.10/tgf/GUID-topologies_and_comm-multi_site_configuration-setting_up_a_multisite_system.html) gateways. GemFire supports Active-Active data updates when using WAN replication between sites. These instructions demonstrate the functionality.

## Start Cluster 1
In Gfsh

Start Locator
```shell
start locator --name=gf1-locator --J=-Dgemfire.distributed-system-id=1    --J=-Dgemfire.remote-locators=127.0.0.1[10002] --enable-cluster-configuration=true --connect=false --port=10001  --J=-Dgemfire.jmx-manager-port=1099 --max-heap=250m --initial-heap=250m --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1
```
Configure PDX

```shell
connect --locator=localhost[10001]
configure gemFireJson --read-serialized=true --disk-store
disconnect
```

```shell
start server --name=gf1-server  --J=-Dgemfire.distributed-system-id=1   --J=-Dgemfire.remote-locators=127.0.0.1[10002] --use-cluster-configuration=true --server-port=10101   --locators=127.0.0.1[10001] --max-heap=1g   --initial-heap=1g  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1
```


-------------------------------------
## Start Cluster 2
In Gfsh

Exit Gfsh

```shell
cd $GEMFIRE_HOME/bin
./gfsh
```

Start Locator
```shell
start locator --name=gf2-locator --J=-Dgemfire.distributed-system-id=2   --J=-Dgemfire.remote-locators=127.0.0.1[10001] --enable-cluster-configuration=true --connect=false  --http-service-port=0 --J=-Dgemfire.tcp-port=11111 --port=10002 --J=-Dgemfire.jmx-manager-port=1098 --max-heap=250m --initial-heap=250m --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1 
```
Configure PDX

```shell
connect --locator=localhost[10002]
configure gemFireJson --read-serialized=true --disk-store
disconnect
```

```shell
start server --name=gf2-server  --J=-Dgemfire.distributed-system-id=2  --J=-Dgemfire.remote-locators=127.0.0.1[10001]  --use-cluster-configuration=true --server-port=10102   --locators=127.0.0.1[10002] --max-heap=1g   --initial-heap=1g  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1
```

--------
--------------------


## Create Gateway receiver to cluster 1

```shell
connect --locator=[10001]
create gateway-sender --id="cluster2" --parallel=true --remote-distributed-system-id=2 --enable-persistence=true 
create region --name=accounts --type=PARTITION --gateway-sender-id="cluster2"
create gateway-receiver --start-port=1510 --end-port=1519
disconnect
```


## Create Gateway receiver to cluster 2


gfsh

```shell
connect --locator=localhost[10002]
create gateway-sender --id="cluster1" --parallel=true --remote-distributed-system-id=1  --enable-persistence=true
create region --name=accounts --type=PARTITION --gateway-sender-id="cluster1"
create gateway-receiver --start-port=1520 --end-port=1521
disconnect
```

------------------------------------------
# WAN Replication - Parallel - Testing

## TEst from cluster 1


```shell
connect --locator=[10001]
put --key="VMW" --value="from-cluster1" --region=accounts
disconnect
```



## TEst from cluster b

```shell
connect --locator=[10002]
get --key="VMW"  --region=accounts
put --key="VMW" --value="from-cluster2" --region=accounts
disconnect
```

# TEst from cluster a
```shell
connect --locator=[10001]
get --key="VMW"  --region=accounts
```

-------------------

# Serial Gateway

Create GW on cluster 1

```shell
connect --locator=[10001]
create gateway-sender --id="cluster2-serial" --parallel=false --remote-distributed-system-id=2 --enable-persistence=true
create region --name=states --type=REPLICATE --gateway-sender-id="cluster2-serial"
disconnect
```


Create GW on cluster 2

```shell
connect --locator=[10002]
create gateway-sender --id="cluster1-serial" --parallel=false --remote-distributed-system-id=1 --enable-persistence=true
create region --name=states --type=REPLICATE --gateway-sender-id="cluster1-serial"
disconnect
```
------------------------------------
# WAN Replication - Serial - Testing

## Test from cluster 1


```shell
connect --locator=[10001]
put --key="NJ" --value="NJ" --region=states
disconnect
```



## TEst from cluster b

```shell
connect --locator=[10002]
get --key="NJ"  --region=states
put --key="LA" --value="LA" --region=states
disconnect
```

# TEst from cluster a
```shell
connect --locator=[10001]
get --key="LA"  --region=accounts
```
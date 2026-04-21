# GemFire Training for System Administrators

**Audience:** System Administrators, DevOps Engineers, and Operations teams that run, monitor, and troubleshoot GemFire clusters in production.

**Prerequisites**

| Knowledge                                                                       | Why it matters                                               |
|---------------------------------------------------------------------------------|--------------------------------------------------------------|
| Basic Linux/Unix administration                                                 | GemFire nodes run on Linux servers                           |
| Networking fundamentals (TCP/UDP, firewalls, ports)                             | GemFire uses several ports (e.g., 7070, 1099, 5000…)         |
| Java fundamentals (JVM, GC, memory)                                             | GemFire is a Java product; you’ll need to interpret JVM logs |
| Familiarity with command‑line utilities (bash and scripting)                    | Many troubleshooting tasks rely on log parsing               |
| Basic understanding of distributed systems concepts (replication, partitioning) | Helps you grasp GemFire internals                            |

> **Goal** – By the end of this course you will be able to:
> * Deploy and manage a GemFire cluster.
> * Use *gfsh* for daily operations, monitoring, and troubleshooting.
> * Interpret GemFire metrics and statistics to find performance bottlenecks.

---

# Start GemFire

1. Run the following script

```shell
./deployment/scripts/podman/labs/start-gemfire-cluster.sh
```

2.Access Gfsh

```shell
deployment/scripts/podman/labs/gfsh.sh
```

3. Connect to Cluster

In gfsh
```gfsh
connect --locator=gf-locator[10334]
```

Create region

```gfsh
create region --name=partioned --type=PARTITION
```

Create region replicated

```gfsh
create region --name=replicated --type=REPLICATE
```

Create partition region with 1 redundant copy of data

```gfsh
create region --name=partioned-redundant --type=PARTITION_REDUNDANT
```


```gfsh
create region --name=partioned-redundant-persistent --type=PARTITION_REDUNDANT_PERSISTENT
```

List disk stores

```shell
list disk-stores
```


Start Bash Shell

```shell
podman run -it -e 'ACCEPT_TERMS=y' --rm --name=gf-bash --network=gemfire-cache  gemfire/gemfire:10.1-jdk21  bash
```


```shell
curl -X PUT \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "role": "Developer"}' \
  "http://gf-server1:7080/gemfire-api/v1/partioned/123"
```

```shell
# Configuration
URL="http://gf-server1:7080/gemfire-api/v1/partioned-redundant-persistent"
COUNTER=0

echo "Starting infinite loop. Press [CTRL+C] to stop."

while true; do
  echo "Beginning batch update of 1,000 items..."
  
  for i in {1..1000}; do
    # Increment global counter to ensure unique keys
    ((COUNTER++))
    
    # Construct JSON payload
    DATA="{\"id\": $COUNTER, \"status\": \"active\", \"timestamp\": $(date +%s)}"
    
    # Execute PUT request
    # -s: Silent mode
    # -o /dev/null: Discard the response body to keep the terminal clean
    # -w: Print the HTTP status code to track success
    curl -s -o /dev/null -w "Key $COUNTER: %{http_code}\n" -X PUT \
         -H "Content-Type: application/json" \
         -d "$DATA" \
         "$URL/$COUNTER" &
         
    # Optional: Small throttle to prevent overwhelming the local thread pool
    if (( i % 100 == 0 )); then
        wait # Wait for background curl processes to finish every 100 requests
    fi
  done
  
  echo "Batch complete. Restarting..."
  sleep 1 # Optional: Pause for 1 second between batches
done
```


---

## Deep Dive: gfsh Commands for Troubleshooting

Below is a quick‑reference cheat sheet you’ll keep on your desk.
Each command is illustrated with a typical output snippet and an interpretation guide.

### 1. `log`

Show Logs in Locator

```gfsh
show log --member=locator1 
```

Given number of lines to display (default 100 lines)

```gfsh
show log --member=server1 --lines=100
```

---

### 2. `show metrics`

Display or export metrics for the entire distributed system, a member or a region.

Show metrics for the cluster

```bash
show metrics
```

Show metrics for a locator

```gfsh
show metrics --member=locator1
```

Show metrics for a cache server

```gfsh
show metrics --member=server1
```


Show metrics by category (ex: serialization, disk store, communication)

```gfsh
show metrics --member=server1 --categories=serialization
```

Save metrics by category to file

```gfsh
show metrics --member=server1 --categories=member --file=/tmp/server-member.csv
```


Open another shell to view metrics in file in podman named "gfsh"

```text
podman exec -it gfsh cat /tmp/server-member.csv
```


*Purpose:* Retrieve all metrics exposed by the GemFire members.
They are grouped by **region**, **gateway**, **locator**, **client-cache**, etc.

**Key metrics to monitor**

| command                   | Metric                      | Typical units | Threshold (example) | What it means                      |
|---------------------------|-----------------------------|---------------|---------------------|------------------------------------|
| show metrics              | totalRegionEntryCount       | > 10M         | Large dataset       |                                    |
| show metrics              | diskWritesRate              | MB/s          | > 50                | Disk pressure                      |
| show metrics --region=... | numBucketsWithoutRedundancy | integer       | > 1                 | Potential Data Loss                |
| show metrics --region=... | totalEntriesOnlyOnDisk      | integer       | > 1                 | Increase latency Insuffient memory |

---

### 3. `show number-of-buckets-without-redundancy`

*Purpose:* Returns the number of partitioned region buckets that have fewer redundant copies than configured.

*Typical output:*

```
Number of buckets without redundancy: 12
```

*Interpretation:*
If you set `redundancy=2` and the output is non‑zero, you risk data loss if a server crashes.

*Action plan:*
1. Identify which members lack redundancy:
2. Restart the affected member(s) or add more servers.
3. Verify the metrics again.


**Interpretation example**

Put data in region

```gfsh
put --key=1 --value=1 --region=/partioned-redundant
put --key=2 --value=2 --region=/partioned-redundant
put --key=3 --value=3 --region=/partioned-redundant
```

```gfsh
show metrics --region=partioned-redundant
```

If `numBucketsWithoutRedundancy` > 1, it’s a sign of missing replicas.


Start Another Server

```bash
./deployment/scripts/podman/labs/start-server-2.sh
```


In gfsh list members

```gfsh
list members
```
Wait for the server2 Status = Ready


Check Number of buckets without rebundancy

```gfsh
show metrics --region=partioned-redundant
```

GemFire Value should now be 0

Note, restarting or adding restore redundancy


Also see gfsh "restore redundancy"

```text
restore redundancy
```

Redistribute data via `rebalance` or add more servers |

See `rebalance` command "Rebalance partitioned regions. The default is for all partitioned regions to be rebalanced."

---

### 4. `show statistics  [--statistics=value(,value)*] [--member=value(,value)*]`

    statistics
        The name of a statistic to show in the form name.type.stat.column


name.type.stat.column


| name                    | 	type                 | 	stat.column              |
|-------------------------|-----------------------|---------------------------|
| PartitionedRegionStats	 | /$regionName          | totalNumBuckets           |
| PartitionedRegionStats	 | /$regionName          | configuredRedundantCopies |
| PartitionedRegionStats	 | /$regionName          | bucketCount               |
| PartitionedRegionStats	 | /$regionName          | primaryBucketCount        |
| PartitionedRegionStats	 | /$regionName          | dataStoreBytesInUse       |
| PartitionedRegionStats	 | /$regionName          | totalNumBuckets           |
| PartitionedRegionStats	 | /$regionName          | actualRedundantCopies     |
| PartitionedRegionStats	 | /$regionName          | configuredRedundantCopies |
| PartitionedRegionStats	 | /$regionName          | totalNumBuckets           |
| PartitionedRegionStats	 | /$regionName          | configuredRedundantCopies |
| DiskStoreStatistics     | 	$diskStoreName       | 	writeTime                |
| DiskStoreStatistics     | 	$diskStoreName       | 	writes                   |
| DistributionStats       | 	distributionStats    | 	nodes                    |
| DistributionStats       | 	distributionStats    | 	functionExecutionThreads |
| DistributionStats       | 	distributionStats    | 	highPriorityThreads      |
| DistributionStats       | 	distributionStats    | 	partitionedRegionThreads |
| StatSampler	            | statSampler	          | delayDuration             |
| StatSampler             | 	statSampler          | 	jvmPauses                |
| VMStats                 | 	vmStats	             | cpus                      |
| VMStats                 | 	vmStats	             | totalMemory               |
| VMStats                 | 	vmStats	             | fdsOpen                   |
| VMStats                 | 	vmStats	             | fdLimit                   |
| VMStats                 | 	vmStats	             | processCpuTime            |
| VMStats                 | 	vmStats	             | threads                   |


```gfsh
show statistics --statistics=VMStats.vmStats.cpus,CachePerfStats.cachePerfStats.queryExecutions --members=server1,server2
```


```gfsh
show statistics --statistics=StatSampler.statSampler.delayDuration --members=server1,server2
```

```gfsh
show statistics --statistics=StatSampler.statSampler.jvmPauses --members=server1,server2
```


```gfsh
show statistics --statistics=PartitionedRegionStats./partioned-redundant-persistent.totalNumBuckets --members=server1,server2
```


```gfsh
show statistics --statistics=PartitionedRegionStats./partioned.bucketCount --members=server1,server2
```

 
> **Also see the VSD tool?**
> *VSD* stands for **Virtual Server Device** – an abstraction of the underlying thread pool that processes cache
operations. VSD stats expose CPU, memory, and thread contention metrics.


---


# Shutdown

```gfsh
shutdown --include-locators
```


You can see stop command

Examples 
```shell
stop locator --name
````

```shell
stop server --name
```

Other utility pods

```shell
podman rm -f gfsh gf-bash
```

----------------

### 5. JVM Monitoring Commands


**Typical JVM metrics to monitor**

| Metric | Threshold | Why it matters |
|--------|-----------|----------------|
| `Used Heap` > 80% | High GC pressure, possible out‑of‑memory |
| `GC pauses` > 5 s | Service degradation, time‑outs |
| `Live Data Ratio` < 20% | Memory leaks or inefficient evictions |

> **Tip:** Use the `-Xmx` flag to set heap size according to your workload, but remember that GemFire has its own
*Off‑Heap* memory region. Configure `-XX:MaxDirectMemorySize` accordingly.

---

## Troubleshooting Flowchart

> **Step 1 – Gather Data**
> 1. Connect via `gfsh`.
> 2. `show status` and `show members`.
> 3. `log tail` for recent errors.
> 4. `show metrics`.
> 5. `show statistics`.

> **Step 2 – Identify the Symptom**
> *Performance lag?* → Check CPU, GC, queue lengths.
> *Data missing?* → Check redundancy, bucket stats.
> *Disk full?* → Check disk metrics and logs.
> *Server crash?* → Look at shutdown logs, GC errors.

> **Step 3 – Drill Down**
> *High GC pauses* → `jcmd <pid> GC.heap_info`, `jstat`.
> *Redundancy issues* → `show number-of-buckets-without-redundancy`.
> *VSD queue overload* → `show vsd-statistics`, `show status`.

> **Step 4 – Mitigate**
> *Add servers* – `gfsh> add server`.
> *Increase memory* – adjust `-Xmx`, `-XX:MaxDirectMemorySize`.
> *Rebalance* – `gfsh> rebalance`.
> *Clear logs* – rotate or purge old logs.
> *Check networking* – ensure ports 7070, 1099, 5000 are open.

> **Step 5 – Verify**
> *Run `show metrics` again* to confirm improvement.
> *Check alerts* – ensure no new high‑severity alerts.

> **Step 6 – Document**
> Record the root cause, actions taken, and future preventive measures in your incident‑management system.

---

## Practical Lab Scenarios

| Lab | Objective | Tools Used |
|-----|-----------|------------|
| **A. Node Failure** | Simulate a server crash; observe data loss risk. | `kill`, `gfsh`, `log`, `show metrics`, `show
number-of-buckets-without-redundancy` |
| **B. Disk Full** | Fill a server’s disk; verify disk‑pressure alerts. | `dd`, `df`, `gfsh`, `log` |
| **C. High GC Pause** | Force a GC pause by allocating a large object. | `jcmd`, `jstat`, `gfsh` |
| **D. Hot‑Spot Data** | Create a single key that is accessed by all clients. | `gfsh`, `show vsd-statistics` |
| **E. Redundancy Recovery** | Delete a server; rebuild redundancy on remaining servers. | `gfsh`, `rebalance`, `show
number-of-buckets-without-redundancy` |

> **Assessment:** After each lab, the participants will present the root cause, the steps taken, and the results.

---

## Best‑Practice Checklist

| Domain | Recommendation                                                                    |
|--------|-----------------------------------------------------------------------------------|
| **Cluster Sizing** | `redundancy = 2` is the minimum for production. Add 20% more servers for hot‑spots. |
| **Memory** | `-Xmx = 70%` of physical RAM, `-XX:MaxDirectMemorySize = 25%` of RAM.             |
| **Disk** | Use separate disks for data (`--disk-store`) and logs. Keep at least 10 % free. |
| **Backups** | Daily incremental snapshots of the `disk-store` directory.                        |
| **Security** | Enable TLS (`--ssl-enabled=true`), use role‑based ACLs.                           |
| **Monitoring** | Export metrics to Prometheus via the GemFire JMX Exporter.                        |
| **Alerting** | Alert thresholds: GC pause > 5 s, redundancy < 100 %, disk free < 10 %.           |
| **Documentation** | Maintain a run‑book for each failure scenario.                                    |
| **Testing** | Run chaos‑testing monthly.                                                        |




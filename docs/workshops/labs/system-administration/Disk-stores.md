# GemFire Disk Store Training
* practical guide for beginners & seasoned users alike*

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


---

## 2. Core Concepts

| Term | Meaning |
|------|---------|
| **Disk Store** | A logical unit that defines where and how region data is written to disk. |
| **Region** | The logical cache container (partitioned, replicated, or local). |
| **Disk Store Properties** | `max-oplog-size`, `max-oplog-disk-space`, `disk-usage-delta`, `write-interval`, etc. |
| **Oplog** | Write‑ahead log file; one per member per disk store. |
| **Snapshot** | Periodic copy of data for fast recovery. |

---

## 3. Quick Setup


---

### 3.2 Create a Disk Store

**Using `gfsh` (GemFire Shell)**


```bash
help create disk-store
```

```gfsh
create disk-store --name=ordersDS --dir=/data/gemfire/diskstore/orders --max-oplog-size=1 --segments=1 
```


List disk store files

```bash
podman exec -it gf-server1 ls -l /data/gemfire/diskstore/orders/ordersDS
```

Notes

| Extension | Description                                                                                                   |
|-----------|---------------------------------------------------------------------------------------------------------------|
| .if       | (Metadata): The "internal file" that tracks the disk store's state and file manifest. Essential for recovery. |
| .crf      | (Data): The "create record file" containing the actual entry values and updates.                              |
| .drf      | (Deletes): The "delete record file" containing IDs of deleted entries to mask them in the .crf.               |
| .krf      | (Key Index): Maps keys to .crf offsets. It isn't required for integrity but makes system startup much faster. |
| .lk       | A lock file to prevent multiple processes from accessing the same store.                                      |


```gfsh
create disk-store --name=accounts  --max-oplog-size=1 --segments=2 --dir=.
```


Create region

```gfsh
create region --name=accounts --disk-store=accounts --type=PARTITION_PERSISTENT
```

Create/Update/Delete Records

```gfsh
put --region=/accounts --key=1 --value=1
put --region=/accounts --key=2 --value=2
put --region=/accounts --key=2 --value="2 update"
put --region=/accounts --key=3 --value=3
remove --region=/accounts --key=3
```

List disk store files in separate shell

```bash
podman exec -it gf-server1 ls -lR accounts/
```


---

### 3.3 Run the Cache

```bash
# Launch a GemFire member
gfsh> start server --name=CacheServer1 --port=40404
```

On startup, GemFire will:

1. Scan the disk directories for oplogs.
2. Re‑hydrate the cache from the most recent snapshot/oplog.
3. Continue writing new entries to the oplogs.

---

## 4. Common Disk Store Properties

| Property | Default | Typical Use |
|----------|---------|-------------|
| `max-oplog-size` | 1 MB | Limits single oplog size before rotation. |
| `max-oplog-disk-space` | Unlimited | Caps total disk usage. |
| `write-interval` | 50 ms | Controls batching of writes to disk. |
| `disk-usage-delta` | 5 % | Triggers eviction when disk usage exceeds this threshold. |
| `enable-snapshot` | true | Enables periodic snapshots for faster recovery. |

> **Recommendation:**  
> *Keep `write-interval` ≥ 50 ms to avoid I/O spikes, unless you have a fast SSD.*

---


---

## 7. Troubleshooting Quick‑Start

| Symptom | Likely Cause | Quick Fix |
|---------|--------------|-----------|
| **Member fails to start** (“disk store not found”) | Disk path wrong or permissions denied | Verify `disk-dirs` path & 
ensure OS permissions. |
| **High disk usage** | Too many oplogs or no snapshots | Enable snapshots, increase `max-oplog-disk-space`. |
| **Slow recovery** | Snapshots disabled or old | Enable snapshots, run `create disk-store --enable-snapshot=true`. |
| **Write latency spikes** | Write‑interval too low | Increase `write-interval`. |
| **Disk full** | Disk‑usage‑delta threshold not hit | Lower `disk-usage-delta` or add more storage. |

---

## 8. Hands‑On Mini‑Project

1. **Create a region** `Orders` with 4 members, persistent.
2. **Simulate traffic** (insert 10k orders per minute).
3. **Force a member crash** (kill one JVM).
4. **Restart** the member and verify all orders re‑appear.

> Use the following Java code snippet:

```java
Cache cache = CacheFactory.create();
cache.createRegionFactory(RegionShortcut.PARTITION_PERSISTENT)
     .setDiskStoreName("ordersDS")
     .create("Orders");

Region<String, Order> orders = cache.getRegion("Orders");
for (int i = 0; i < 10000; i++) {
    orders.put("order-" + i, new Order(i, "customer-" + i));
}
```

---

## 9. Resources

| Link | What it covers |
|------|----------------|
| **GemFire Docs – Disk Store** | Full reference + examples |
| **Apache Geode – Persistence** | Open‑source equivalent |
| **GemFire Manager** | Visual monitoring & alerting |
| **Community Forum** | Real‑world scenarios & scripts |

---

## 10. Quick Recap Checklist

| ✅ | Item |
|---|------|
| 1 | Disk store defined (`create disk-store`). |
| 2 | Region created with `*_PERSISTENT` shortcut. |
| 3 | `max-oplog-disk-space` and `write-interval` tuned. |
| 4 | `enable-snapshot=true`. |
| 5 | Disk directories have proper permissions. |
| 6 | Disk usage monitored via GemFire Manager. |

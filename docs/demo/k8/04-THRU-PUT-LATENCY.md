# Concurrency, Latency and Throughput

```shell
kubectl exec  -n perftest -it gemfire1-locator-0 -- gfsh -e connect -e "create region --name=test --type=PARTITION_PERSISTENT"
```

```shell
k port-forward gemfire1-locator-0 7071:7070 -n perftest
```
```shell
cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/spring-geode-showcase
```

```shell
k apply  -n perftest -f cloud/k8/apps/geode-perf-test/put
```

```shell
k logs -n perftest -f jobs/geode-perf-test-put-01 -c apache-geode-perf-test-put-01 -c apache-geode-perf-test-put-02 -c apache-geode-perf-test-put-03 
```

-----------

## Get latency 

```shell
k apply -n perftest -f cloud/k8/apps/geode-perf-test/get 
```


```shell
k logs  -n perftest -f jobs/geode-perf-test-get
```
-----------
Cleanup

```shell
k delete -n perftest -f cloud/k8/apps/geode-perf-test/put 
k delete -n perftest -f cloud/k8/apps/geode-perf-test/get
```

```shell
kubectl exec  -n perftest -it gemfire1-locator-0 -- gfsh -e connect -e "destroy region --name=test"
```

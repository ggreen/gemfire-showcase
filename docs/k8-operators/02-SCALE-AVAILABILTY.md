
# SCALE and Availability

```shell
cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase
```

## Change directory to where the example Spring Boot applications

```shell
kubectl apply -f deployment/cloud/k8/data-services/gemfire/gemfire1-2loc-2data.yml
```

```yaml
apiVersion: gemfire.tanzu.vmware.com/v1
kind: GemFireCluster
metadata:
  name: gemfire1
spec:
  image: registry.pivotal.io/tanzu-gemfire-for-kubernetes/gemfire-k8s:1.0.1
  locators:
    replicas: 1
    overrides:
      gemfireProperties:
        distributed-system-id: "1"
  servers:
    replicas: 2
    overrides:
      jvmOptions:
        - "-Djava.awt.headless=true"
        - "-Dsun.rmi.dgc.server.gcInterval=9223372036854775806"
        - "-XX:+UseG1GC"
        - "-XX:+PrintGCDetails"
        - "-XX:MaxGCPauseMillis=40"
        - "-Xms674m"
        - "-Xmx674m"
        - "-Dgemfire.statistic-sample-rate=5000"
        - "-Dgemfire.enable-time-statistics=true"
        - "-Dgemfire.statistic-sampling-enabled=true"
        - "-Dgemfire.standard-output-always-on=true"
        - "-Dgemfire.archive-file-size-limit=10"
        - "-Dgemfire.conserve-sockets=false"
        - "-Dgemfire.prometheus.metrics.port=4321"
        - "-Dgemfire.log-disk-space-limit=409"
        - "-Dgemfire.archive-disk-space-limit=409"
        - "-Dgemfire.log-file-size-limit=100"
        - "-Dgemfire.locator-wait-time=120"
        - "-Dgemfire.ALLOW_PERSISTENT_TRANSACTIONS=true"
      gemfireProperties:
        distributed-system-id: "1"
```


Look for gemfire1-locator-0, gemfire1-server-0 gemfire1-server-1
```shell
watch kubectl get pods
```

## login into the GemFire cluster using gfsh

```shell
kubectl exec -it gemfire1-locator-0 -- gfsh -e "connect" -e "create region --name=Account --type=PARTITION_REDUNDANT_PERSISTENT"
```


--------------------------
# Deploy applications

##  - Deploy the definitions located in cloud/k8/apps/app.yml

```shell
kubectl apply -f cloud/k8/apps 
```

##  - use the watch command util the application account-rest-service pod state is ready   (Control^C to stop)

```shell
watch kubectl get pods
```

##  - Expose the spring-geode-showcase to be accessed using port 8080

```shell
kubectl port-forward deployment/account-rest-service 8080:8080
```

##  - Write account data

```shell
curl -X 'POST' \
'http://localhost:8080/accounts' \
-H 'accept: */*' \
-H 'Content-Type: application/json' \
-d '{ "id": "1", "name": "Acct 1" }'  ; echo
```

##  - Read account data

```shell
curl -X 'GET' 'http://localhost:8080/accounts/1' -H 'accept: */*'  ; echo
```


```shell
kubectl exec -it gemfire1-locator-0 -- gfsh
```

```shell
connect
list members
list clients
```

--------------------------
# K8 Auto Healing

##  - Delete/Kill the cache server data node (may take several seconds)

```shell
k delete pod gemfire1-server-0
```


```shell
kubectl exec -it gemfire1-locator-0 -- gfsh -e "connect" -e "list members"
```

##  - watch the kubernetes platform recreate the deleted server (Control^C to stop)

```shell
watch kubectl get pods
```


##  - Try to Read account 

```shell
curl -X 'GET' 'http://localhost:8080/accounts/1' -H 'accept: */*'  ; echo
```
--------------

## apply configuration to add one additional locator

```shell
k apply -f  cloud/k8/data-services/gemfire/gemfire1-2loc-2server.yml
```

```yaml
apiVersion: gemfire.tanzu.vmware.com/v1
kind: GemFireCluster
metadata:
  name: gemfire1
spec:
  image: registry.pivotal.io/tanzu-gemfire-for-kubernetes/gemfire-k8s:1.0.1
  locators:
    replicas: 2
    overrides:
      gemfireProperties:
        distributed-system-id: "1"
  servers:
    replicas: 2
    overrides:
      jvmOptions:
        - "-Djava.awt.headless=true"
        - "-Dsun.rmi.dgc.server.gcInterval=9223372036854775806"
        - "-XX:+UseG1GC"
        - "-XX:+PrintGCDetails"
        - "-XX:MaxGCPauseMillis=40"
        - "-Xms674m"
        - "-Xmx674m"
        - "-Dgemfire.statistic-sample-rate=5000"
        - "-Dgemfire.enable-time-statistics=true"
        - "-Dgemfire.statistic-sampling-enabled=true"
        - "-Dgemfire.standard-output-always-on=true"
        - "-Dgemfire.archive-file-size-limit=10"
        - "-Dgemfire.conserve-sockets=false"
        - "-Dgemfire.prometheus.metrics.port=4321"
        - "-Dgemfire.log-disk-space-limit=409"
        - "-Dgemfire.archive-disk-space-limit=409"
        - "-Dgemfire.log-file-size-limit=100"
        - "-Dgemfire.locator-wait-time=120"
        - "-Dgemfire.ALLOW_PERSISTENT_TRANSACTIONS=true"
      gemfireProperties:
        distributed-system-id: "1"
```

##  - wait for gemfire1-locator-1 state to be ready and running (control^C to stop)

```shell
watch kubectl get pods
```

##  - Read data in a loop to check resiliency due to 

```shell
for i in $(seq 1 2000);
do
curl -X 'GET' 'http://localhost:8080/accounts/1'  -H 'accept: */*'  ; echo
sleep 1s
done
```

```shell
k delete pod gemfire1-locator-0
```
See the loop shell from  (should not see any errors)  (Control^C to stop)

```shell
watch kubectl get pods
```



-------------------------------------------
# Scale Data Node/Cache Server

```shell
cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/spring-geode-showcase
```

##  apply configuration to add two additional data node/cache server

```shell
k apply -f cloud/k8/data-services/gemfire/gemfire1-2loc-3data.yml
```

```yaml
apiVersion: gemfire.tanzu.vmware.com/v1
kind: GemFireCluster
metadata:
  name: gemfire1
spec:
  image: registry.pivotal.io/tanzu-gemfire-for-kubernetes/gemfire-k8s:1.0.1
  locators:
    replicas: 2
    overrides:
      gemfireProperties:
        distributed-system-id: "1"
  servers:
    replicas: 3
    overrides:
      jvmOptions:
        - "-Djava.awt.headless=true"
        - "-Dsun.rmi.dgc.server.gcInterval=9223372036854775806"
        - "-XX:+UseG1GC"
        - "-XX:+PrintGCDetails"
        - "-XX:MaxGCPauseMillis=40"
        - "-Xms674m"
        - "-Xmx674m"
        - "-Dgemfire.statistic-sample-rate=5000"
        - "-Dgemfire.enable-time-statistics=true"
        - "-Dgemfire.statistic-sampling-enabled=true"
        - "-Dgemfire.standard-output-always-on=true"
        - "-Dgemfire.archive-file-size-limit=10"
        - "-Dgemfire.conserve-sockets=false"
        - "-Dgemfire.prometheus.metrics.port=4321"
        - "-Dgemfire.log-disk-space-limit=409"
        - "-Dgemfire.archive-disk-space-limit=409"
        - "-Dgemfire.log-file-size-limit=100"
        - "-Dgemfire.locator-wait-time=120"
        - "-Dgemfire.ALLOW_PERSISTENT_TRANSACTIONS=true"
      gemfireProperties:
        distributed-system-id: "1"
```


Or 

```shell
k edit gemfirecluster gemfire1
```

##  wait for the addition gemfire1-server (1-2) states to be ready and running (control^C to stop)

```shell
watch kubectl get pods
```


```shell
k delete pod gemfire1-server-1
```

k delete -f cloud/k8/data-services/gemfire/gemfire1-2loc-2server.yml
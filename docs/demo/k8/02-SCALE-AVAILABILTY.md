
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
kubectl get configmap gemfire1-config -o yaml
```

```shell
kubectl exec -it gemfire1-locator-0 -- gfsh -e "connect --locator=gemfire1-locator-0.gemfire1-locator.tanzu-data.svc.cluster.local[10334]" -e "create region --name=Account --type=PARTITION_REDUNDANT_PERSISTENT"
```


--------------------------
# Deploy applications

##  - Deploy Account Service GemFire client

```shell
kubectl apply -f deployment/cloud/k8/apps/account-service/account-service.yml
```

##  - use the watch command util the application account-rest-service pod state is ready   (Control^C to stop)

```shell
watch kubectl get pods
```

##  - Expose the spring-geode-showcase to be accessed using port 8080

```shell
export API_HTTP_HOST=`kubectl get services account-service-gemfire-showcase --output jsonpath='{.status.loadBalancer.ingress[0].ip}'`
```

##  - Write account data

```shell
curl -X 'POST' \
"http://$API_HTTP_HOST:8080/accounts" \
-H 'accept: */*' \
-H 'Content-Type: application/json' \
-d '{ "id": "1", "name": "Acct 1" }'  ; echo
```

##  - Read account data

```shell
curl -X 'GET' "http://$API_HTTP_HOST:8080/accounts/1" -H 'accept: */*'  ; echo
```


```shell
kubectl exec -it gemfire1-locator-0 -- gfsh
```

```shell
connect --locator=gemfire1-locator-0.gemfire1-locator.tanzu-data.svc.cluster.local[10334]
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
curl -X 'GET' "http://$API_HTTP_HOST:8080/accounts/1" -H 'accept: */*'  ; echo
```

-------------------------------------------
# Scale Data Node/Cache Server

```shell
cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase
```

##  apply configuration to add two additional data node/cache server

```shell
kubectl apply -f deployment/cloud/k8/data-services/gemfire/gemfire1-2loc-3data.yml
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
kubectl get pods -w
```

##  - Try to Read account

```shell
curl -X 'GET' "http://$API_HTTP_HOST:8080/accounts/1" -H 'accept: */*'  ; echo
```

##  - Write account data

```shell
curl -X 'POST' \
"http://$API_HTTP_HOST:8080/accounts" \
-H 'accept: */*' \
-H 'Content-Type: application/json' \
-d '{ "id": "2", "name": "Acct 2" }'  ; echo
```

```shell
curl -X 'GET' "http://$API_HTTP_HOST:8080/accounts/2" -H 'accept: */*'  ; echo
```




```shell
k delete pod gemfire1-server-1
```

k delete -f cloud/k8/data-services/gemfire/gemfire1-2loc-2server.yml
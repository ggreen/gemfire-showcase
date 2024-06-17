# Recoverability

## - Apply configuration to create addition GemFire cluster


```shell
kubectl create namespace tanzu-data-wan
kubectl config set-context --current --namespace=tanzu-data-wan
```


```shell
kubectl apply -f deployment/cloud/k8/data-services/gemfire/WAN-replication/gemfire_cluster_a.yml
kubectl apply -f deployment/cloud/k8/data-services/gemfire/WAN-replication/gemfire_cluster_b.yml
```


## Wait for pods (2 Locators gemfire2-locator(0-1) and 2 Data node gemfire2-server(0-2) to be ready  (Control^C to stop)

```shell
kubectl get pods -w
```

```shell
kubectl exec -it cluster-a-locator-0 --  gfsh
```



Setup Cluster Gateways

```shell
./deployment/cloud/k8/data-services/gemfire/scripts/gf-wan-setup.sh
```

```shell
kubectl get pods -w
```

----------------

# Deploy acct services

```shell
kubectl apply -f deployment/cloud/k8/apps/account-service/wan-ha/account-service-cluster-a.yml
```

```shell
kubectl apply -f deployment/cloud/k8/apps/account-service/wan-ha/account-service-cluster-b.yml
```

# Deployment Gateway

```shell
kubectl apply -f deployment/cloud/k8/apps/spring-gateway-healthcheck/spring-gateway-healthcheck.yml
```


```shell
k get pods -w 
```

```shell
export SPRING_GATEWAY_HOST=`kubectl get services spring-gateway-healthcheck --output jsonpath='{.status.loadBalancer.ingress[0].ip}'`
export ACCT_CLUSTER_A_HOST=`kubectl get services account-service-cluster-a --output jsonpath='{.status.loadBalancer.ingress[0].ip}'`
export ACCT_CLUSTER_B_HOST=`kubectl get services account-service-cluster-b --output jsonpath='{.status.loadBalancer.ingress[0].ip}'`
```


```shell
echo SPRING_GATEWAY_HOST=$SPRING_GATEWAY_HOST
echo ACCT_CLUSTER_A_HOST=$ACCT_CLUSTER_A_HOST
echo ACCT_CLUSTER_B_HOST=$ACCT_CLUSTER_B_HOST
```


Save account 1

```shell
curl -X 'POST' \
  "http://$SPRING_GATEWAY_HOST:8080/accounts" \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id": "1",
  "name": "1"
}'
```


Save account 2

```shell
curl "http://$SPRING_GATEWAY_HOST:8080/accounts" \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id": "2",
  "name": "2"
}'
```

Get Account 2
```shell
curl -X 'GET' \
  "http://$SPRING_GATEWAY_HOST:8080/accounts/2" \
  -H 'accept: */*'
```

Test Actuator for App Service to cluster 1 expected "UP"
```shell
curl http://$ACCT_CLUSTER_A_HOST:8080/actuator/health
```

Test Actuator for App Service to cluster 2 expected "UP"
```shell
curl http://$ACCT_CLUSTER_B_HOST:8080/actuator/health
```

Stop Cluster 1 Servers

```shell
kubectl exec cluster-a-locator-0 --  gfsh -e "connect --locator=cluster-a-locator-0.cluster-a-locator.tanzu-data.svc.cluster.local[10334]" -e "shutdown"
```


Check app service to cluster 1 expected "DOWN"
```shell
curl http://$ACCT_CLUSTER_A_HOST:8080/actuator/health
```

Test Actuator for App Service to cluster 2 expected "UP"
```shell
curl http://$ACCT_CLUSTER_B_HOST:8080/actuator/health
```

Get Account 1
```shell
curl http://$SPRING_GATEWAY_HOST:8080/accounts/1
```


Get Account 2
```shell
curl http://$SPRING_GATEWAY_HOST:8080/accounts/2
```

Server 1
```shell
$GEMFIRE_HOME/bin/gfsh -e "start server --name=gf1-server --use-cluster-configuration=true --server-port=10101   --locators=127.0.0.1[10001] --max-heap=1g   --initial-heap=1g  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1  --J=-Dgemfire.distributed-system-id=1"
```

Sync data in cluster 2 to 1

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]" -e "wan-copy region --region=/Account --sender-id=Account_Sender_to_1"
```

Get Account 1
```shell
curl http://$SPRING_GATEWAY_HOST:8080/accounts/1
```

Get Account 2
```shell
curl http://$SPRING_GATEWAY_HOST:8080/accounts/2
```

Shutdown cluster 2

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]" -e "shutdown --include-locators"
```
Test Actuator for App Service to cluster 1 expected "UP"
```shell
curl http://$ACCT_CLUSTER_A_HOST:8080/actuator/health
```

Test Actuator for App Service to cluster 2 expected "DOWN"
```shell
curl http://$ACCT_CLUSTER_B_HOST:8080/actuator/health
```

Get Account 1
```shell
curl http://$SPRING_GATEWAY_HOST:8080/accounts/1
```

Get Account 2
```shell
curl http://$SPRING_GATEWAY_HOST:8080/accounts/2
```


--------------

# Clean

```shell
kubectl delete -f deployment/cloud/k8/data-services/gemfire/WAN-replication/gemfire_cluster_a.yml
kubectl delete -f deployment/cloud/k8/data-services/gemfire/WAN-replication/gemfire_cluster_b.yml
kubectl delete -f deployment/cloud/k8/apps/account-service/wan-ha/account-service-cluster-a.yml
kubectl delete -f deployment/cloud/k8/apps/account-service/wan-ha/account-service-cluster-b.yml
kubectl delete -f deployment/cloud/k8/apps/spring-gateway-healthcheck/spring-gateway-healthcheck.yml
```
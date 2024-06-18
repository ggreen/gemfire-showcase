# Recoverability

## - Apply configuration to create addition GemFire cluster




Setup Cluster Gateways


```shell
./deployment/cloud/k8/data-services/gemfire/scripts/gf-wan-setup.sh
```

```shell
kubectl get pods -A | grep tanzu-data-site
```

----------------

# Deploy acct services

# Deployment Gateway

```shell

```


```shell
k get pods -w 
```

```shell
export SPRING_GATEWAY_HOST=`kubectl get services spring-gateway-healthcheck --output jsonpath='{.status.loadBalancer.ingress[0].ip}' --namespace=tanzu-data-wan`
export ACCT_CLUSTER_A_HOST=`kubectl get services account-service-cluster-a --output jsonpath='{.status.loadBalancer.ingress[0].ip}' --namespace=tanzu-data-site-1`
export ACCT_CLUSTER_B_HOST=`kubectl get services account-service-cluster-b --output jsonpath='{.status.loadBalancer.ingress[0].ip}' --namespace=tanzu-data-site-2`
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

Get Account 1
```shell
curl -X 'GET' \
  "http://$SPRING_GATEWAY_HOST:8080/accounts/1" \
  -H 'accept: */*'
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
kubectl delete pod  gemfire-cluster-a-locator-0 gemfire-cluster-a-server-0  gemfire-cluster-a-server-1 --force=true  --namespace=tanzu-data-site-1
```


Get Account 1
```shell
curl http://$SPRING_GATEWAY_HOST:8080/accounts/1
```


Get Account 2
```shell
curl http://$SPRING_GATEWAY_HOST:8080/accounts/2
```



Check app service to cluster 1 expected "DOWN" or Internal Error
```shell
curl http://$ACCT_CLUSTER_A_HOST:8080/actuator/health
```

Test Actuator for App Service to cluster 2 expected "UP"
```shell
curl http://$ACCT_CLUSTER_B_HOST:8080/actuator/health
```

Save account another account


```shell
curl "http://$SPRING_GATEWAY_HOST:8080/accounts" \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id": "4",
  "name": "4"
}'
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


-------------

```shell
kubectl config set-context --current --namespace=tanzu-data-site-1
kubectl exec gemfire-cluster-a-locator-0 --  gfsh -e "connect --locator=gemfire-cluster-a-locator-0.gemfire-cluster-a-locator.tanzu-data-site-1.svc.cluster.local[10334]" -e "shutdown"
```

```shell
kubectl config set-context --current --namespace=tanzu-data-site-2
kubectl exec gemfire-cluster-b-locator-0 -it --  gfsh 
```

-e "connect --locator=gemfire-cluster-b-locator-0.gemfire-cluster-b-locator.tanzu-data-site-2.svc.cluster.local[10334]" -e "shutdown"


--------------

# Clean

```shell
kubectl delete -f deployment/cloud/k8/data-services/gemfire/WAN-replication/gemfire_cluster_a.yml --namespace=tanzu-data-site-1
kubectl delete -f deployment/cloud/k8/apps/account-service/wan-ha/account-service-cluster-a.yml --namespace=tanzu-data-site-1
kubectl delete -f deployment/cloud/k8/data-services/gemfire/WAN-replication/gemfire_cluster_b.yml  --namespace=tanzu-data-site-2
kubectl delete -f deployment/cloud/k8/apps/account-service/wan-ha/account-service-cluster-b.yml  --namespace=tanzu-data-site-2
kubectl delete -f deployment/cloud/k8/apps/spring-gateway-healthcheck/spring-gateway-healthcheck.yml
```
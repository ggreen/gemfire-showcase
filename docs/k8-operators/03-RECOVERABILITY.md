# Recoverability

## - Apply configuration to create addition GemFire cluster

```shell
k apply -f cloud/k8/data-services/oltp/gemfire1-2loc-3data.yml
k apply -f cloud/k8/data-services/oltp/gemfire2-2loc-3data.yml
```


## Wait for pods (2 Locators gemfire2-locator(0-1) and 2 Data node gemfire2-server(0-2) to be ready  (Control^C to stop)

```shell
watch kubectl get pods
```
export GF1_TLS_PASSWORD=$(kubectl -n default get secret gemfire1-cert -o=jsonpath='{.data.password}' | base64 -D)
export GF2_TLS_PASSWORD=$(kubectl -n default get secret gemfire2-cert -o=jsonpath='{.data.password}' | base64 -D)


## - Create Gateway receiver to cluster 1 

```shell
kubectl exec -it gemfire1-locator-0 -- gfsh -e "connect --locator=gemfire1-locator-0.gemfire1-locator.default.svc.cluster.local[10334] --trust-store=/certs/truststore.p12 --trust-store-password=$GF1_TLS_PASSWORD --key-store=/certs/keystore.p12 --key-store-password=$GF1_TLS_PASSWORD" -e "create gateway-receiver"
```

## - Gateway sender from cluster 2 to cluster 1 for account region


```shell
kubectl exec -it gemfire2-locator-0 -- gfsh -e "connect --locator=gemfire1-locator-0.gemfire1-locator.default.svc.cluster.local[10334] --trust-store=/certs/truststore.p12 --trust-store-password=$GF2_TLS_PASSWORD --key-store=/certs/keystore.p12 --key-store-password=$GF2_TLS_PASSWORD"  -e "create gateway-sender --id=Account_Sender_to_1 --parallel=true  --remote-distributed-system-id=1 --enable-persistence=true --enable-batch-conflation=true"
```

## - Create Account region to use the Gateway sender from cluster 2 to cluster 1

```shell
kubectl exec -it gemfire2-locator-0 -- gfsh -e "connect --locator=gemfire1-locator-0.gemfire1-locator.default.svc.cluster.local[10334] --trust-store=/certs/truststore.p12 --trust-store-password=$GF2_TLS_PASSWORD --key-store=/certs/keystore.p12 --key-store-password=$GF2_TLS_PASSWORD" -e "create region --name=Account --type=PARTITION_REDUNDANT_PERSISTENT --gateway-sender-id=Account_Sender_to_1"
```

## - Create Location region to use the Gateway sender from cluster 2 to cluster 1

```shell
kubectl exec -it gemfire2-locator-0 -- gfsh -e connect -e "create region --name=Location --type=PARTITION_REDUNDANT_PERSISTENT --colocated-with=/Account  --gateway-sender-id=Account_Sender_to_1"
```


----------------------------------------------------------
# Deployment Application to access GemFire Cluster 2

## - deploy spring-geode-kotlin-transaction app to use GemFire cluster 2

```shell
k apply -f cloud/k8/apps/wan/app-transactions-wan2.yml
```

## - Wait for spring-geode-kotlin-transaction-wan2 to be running  (Control^C to stop)

```shell
watch kubectl get pods
```

## - Deploy spring-geode-showcase application to use GemFire cluster 2

```shell
k apply -f cloud/k8/apps/wan/app-wan2.yml
```

## - Export port of the spring-geode-kotlin-transaction-wan2 app

```shell
kubectl port-forward deployment/account-location-rest-service-wan2 9290:8080
```


----------------------------------------------------------
# Verify WAN replication

## - Read from spring-geode-showcase connect to clusrer 1
Response should be Empty/null

```shell
curl -X 'GET' 'http://localhost:8080/accounts/ACCT-WAN' -H 'accept: */*'  ; echo
```


## - Write data into cluster 2 using a transaction

```shell
curl -X 'POST' \
'http://localhost:9290/accounts' \
-H 'accept: */*' \
-H 'Content-Type: application/json' \
-d '{ "account": { "id": "WAN", "name": "Account WAN" }, "location": { "id": "WAN", "address": "123 WAN Street-WAN", "city": "NYC", "stateCode": "NY","zipCode": "55555"} }'  ; echo
```

## - Read the WAN replicated data from cluster 1

```shell
curl -X 'GET' 'http://localhost:8080/accounts/WAN' -H 'accept: */*'  ; echo
```

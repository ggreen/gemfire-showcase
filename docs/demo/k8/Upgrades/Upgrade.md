
Deploy GemFire Cluster version 10.1

```properties
kubectl apply -f deployment/cloud/k8/data-services/gemfire/gemfire1-2loc-2data.yml
```

Setup GMC

```shell
kubectl apply -f deployment/cloud/k8/data-services/gemfire/gideonConsole/gemfire-management-console.yml
```


```properties
name=gemfire
host=gemfire1-locator-0
port=7070
```
Port Forward

```shell
kubectl port-forward service/gemfire-management-console --namespace=tanzu-data 8080:8080 
```


start gfsh

```properties
kubectl  exec -it gemfire1-locator-0 -- gfsh 
```

```shell
connect --locator=gemfire1-locator-0.gemfire1-locator.tanzu-data.svc.cluster.local[10334],gemfire1-locator-1.gemfire1-locator.tanzu-data.svc.cluster.local[10334]
```

```properties
kubectl port-forward service/gemfire-management-console --namespace=tanzu-data 8080:8080
```

OpenUI

```shell
open http://localhost:8080
```

```properties
 kubectl get GemFireCluster
```


```shell
kubectl  exec -it gemfire1-locator-0 -- gfsh -e "connect --locator=gemfire1-locator-0.gemfire1-locator.tanzu-data.svc.cluster.local[10334]" -e "create region --name=Properties --type=REPLICATE"

kubectl  exec -it gemfire1-locator-0 -- gfsh -e "connect --locator=gemfire1-locator-0.gemfire1-locator.tanzu-data.svc.cluster.local[10334]" -e "create region --name=Account --type=REPLICATE"

```

Deploy App

```shell
kubectl apply -f deployment/cloud/k8/apps/account-service/account-service.yml
```

Logs 

```shell
kubectl logs -l name=account-service-gemfire-showcase -f
```

```properties
kubectl port-forward service/account-service-gemfire-showcase --namespace=tanzu-data 8090:8080
```

Open

```shell
open http://localhost:8090/swagger-ui/index.html
```

```json
{
  "id": "01",
  "name": "acct 1"
}
```

Get data
```shell
curl -X 'GET' \
  'http://localhost:8090/accounts/01' \
  -H 'accept: */*'
```


Upgrade GemFire

```shell
kubectl get GemFireCluster
```


```shell
kubectl edit GemFireCluster gemfire1
```


Update image

```shell
 image: gemfire/gemfire:10.2
```

Get data
```shell
while true
do
  echo "This will loop forever...";
  curl -X 'GET'  'http://localhost:8090/accounts/01' -H 'accept: */*'; echo;
  
  sleep 2
done
```


View Cluster in GemFire Management Cluster



# Recoverability

## - Apply configuration to create addition GemFire cluster

```shell
k apply -f cloud/k8/data-services/gemfire/wan-replication/gemfire_cluster_a.yml
k apply -f cloud/k8/data-services/gemfire/wan-replication/gemfire_cluster_b.yml
```


## Wait for pods (2 Locators gemfire2-locator(0-1) and 2 Data node gemfire2-server(0-2) to be ready  (Control^C to stop)


kubectl exec -it cluster-a-locator-0 -n default --  bash


hostname=cluster-a-locator-0.cluster-a-locator.default.svc.cluster.local

gfsh

connect --locator=cluster-a-locator-0.cluster-a-locator.default.svc.cluster.local[10334]

```shell
watch kubectl get pods
```
export GF1_TLS_PASSWORD=$(kubectl -n default get secret gemfire1-cert -o=jsonpath='{.data.password}' | base64 -D)
export GF2_TLS_PASSWORD=$(kubectl -n default get secret gemfire2-cert -o=jsonpath='{.data.password}' | base64 -D)


## - Create Gateway receiver to cluster a

```shell
create gateway-sender --id="clusterb" --parallel=true --remote-distributed-system-id=2 
create region --name=stocks --type=PARTITION --gateway-sender-id="clusterb"

```


## - Create Gateway receiver to cluster b

kubectl exec -it cluster-b-locator-0 -n default --  bash

gfsh

hostname=cluster-b-locator-0.cluster-b-locator.default.svc.cluster.local


connect --locator=cluster-b-locator-0.cluster-b-locator.default.svc.cluster.local[10334]

create region --name=stocks --type=PARTITION

create gateway-receiver --start-port=1530 --end-port=1551


# TEst from cluster a


put --key="VMW" --value="113" --region=stocks


# TEst from cluster b

get --key="VMW"  --region=stocks


#--------------------

# Scaling Servers

k edit GemFireCluster cluster-a

k edit GemFireCluster cluster-b




------------------

# Cleanup

k delete pvc data-cluster-a-locator-0  data-cluster-a-server-0  data-cluster-a-server-1  data-cluster-a-server-2  data-cluster-b-locator-0  data-cluster-b-server-0  data-cluster-b-server-1
# WAN Replication

Configuraremos dos clústeres de GemFire

## - Aplicar la configuración para crear un clúster adicional de GemFire


```shell
cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/spring-geode-showcase
```

```shell
k apply -f cloud/k8/data-services/gemfire/wan-replication/gemfire_cluster_a.yml
k apply -f cloud/k8/data-services/gemfire/wan-replication/gemfire_cluster_b.yml
```

**Crea un clúster GemFire ​​de múltiples miembros en minutos, con capacidades de escalado y recuperación automática**

## Conéctese al clúster A en AWS

```shell
kubectl exec -it cluster-a-locator-0 -n default --  bash
```

Nota nombre de host
hostname=cluster-a-locator-0.cluster-a-locator.default.svc.cluster.local

```shell
gfsh
```
Conéctese al clúster a través del localizador

- tenga en cuenta que puede configurar TLS y AUTHZ personalizado. TLS está habilitado de forma predeterminada, pero para este ejemplo está deshabilitado 

```shell
connect --locator=cluster-a-locator-0.cluster-a-locator.default.svc.cluster.local[10334]
```


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




```shell
export GF1_TLS_PASSWORD=$(kubectl -n default get secret gemfire1-cert -o=jsonpath='{.data.password}' | base64 -D)
export GF2_TLS_PASSWORD=$(kubectl -n default get secret gemfire2-cert -o=jsonpath='{.data.password}' | base64 -D)

```

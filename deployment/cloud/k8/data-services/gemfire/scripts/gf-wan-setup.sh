kubectl create namespace tanzu-data-site-1
kubectl create secret docker-registry image-pull-secret --namespace=tanzu-data-site-1 --docker-server=registry.packages.broadcom.com --docker-username=$BROADCOM_USERNAME --docker-password=$BROADCOM_GEMFIRE_PASSWORD
kubectl config set-context --current --namespace=tanzu-data-site-1

kubectl apply -f deployment/cloud/k8/data-services/gemfire/WAN-replication/gemfire_cluster_a.yml

sleep 5

kubectl wait pod -l=app.kubernetes.io/component=gemfire-locator --for=condition=Ready --timeout=160s  --namespace=tanzu-data-site-1

sleep 5

kubectl wait pod -l=statefulset.kubernetes.io/pod-name=gemfire-cluster-a-server-0 --for=condition=Ready --timeout=160s  --namespace=tanzu-data-site-1

sleep 5
kubectl wait pod -l=statefulset.kubernetes.io/pod-name=gemfire-cluster-a-server-1 --for=condition=Ready --timeout=160s  --namespace=tanzu-data-site-1



kubectl exec gemfire-cluster-a-locator-0 --  gfsh -e "connect --locator=gemfire-cluster-a-locator-0.gemfire-cluster-a-locator.tanzu-data-site-1.svc.cluster.local[10334]" -e "create gateway-receiver"  -e  "create gateway-sender --id=Account_Sender_to_2 --parallel=true  --remote-distributed-system-id=2 --enable-persistence=true --enable-batch-conflation=true"

kubectl exec gemfire-cluster-a-locator-0 --  gfsh -e "connect --locator=gemfire-cluster-a-locator-0.gemfire-cluster-a-locator.tanzu-data-site-1.svc.cluster.local[10334]"  -e  "create region --name=Account --type=PARTITION_PERSISTENT --gateway-sender-id=Account_Sender_to_2"


kubectl exec gemfire-cluster-a-locator-0 --  gfsh -e "connect --locator=gemfire-cluster-a-locator-0.gemfire-cluster-a-locator.tanzu-data-site-1.svc.cluster.local[10334]"  -e  "create region --name=health1 --type=PARTITION"

#------
# Cluster 2
kubectl create namespace tanzu-data-site-2
kubectl create secret docker-registry image-pull-secret --namespace=tanzu-data-site-2 --docker-server=registry.packages.broadcom.com --docker-username=$BROADCOM_USERNAME --docker-password=$BROADCOM_GEMFIRE_PASSWORD
kubectl config set-context --current --namespace=tanzu-data-site-2

kubectl apply -f deployment/cloud/k8/data-services/gemfire/WAN-replication/gemfire_cluster_b.yml

sleep 10
kubectl wait pod -l=app.kubernetes.io/component=gemfire-locator --for=condition=Ready --timeout=160s  --namespace=tanzu-data-site-2

sleep 5
kubectl wait pod -l=statefulset.kubernetes.io/pod-name=gemfire-cluster-b-server-0 --for=condition=Ready --timeout=160s  --namespace=tanzu-data-site-2

sleep 5
kubectl wait pod -l=statefulset.kubernetes.io/pod-name=gemfire-cluster-b-server-1 --for=condition=Ready --timeout=160s  --namespace=tanzu-data-site-2


kubectl exec  gemfire-cluster-b-locator-0 --  gfsh -e "connect --locator=gemfire-cluster-b-locator-0.gemfire-cluster-b-locator.tanzu-data-site-2.svc.cluster.local[10334]" -e  "create gateway-receiver" -e  "create gateway-sender --id=Account_Sender_to_1 --parallel=true  --remote-distributed-system-id=1 --enable-persistence=true --enable-batch-conflation=true"


kubectl exec  gemfire-cluster-b-locator-0 --  gfsh -e "connect --locator=gemfire-cluster-b-locator-0.gemfire-cluster-b-locator.tanzu-data-site-2.svc.cluster.local[10334]"  -e  "create region --name=Account --type=PARTITION_PERSISTENT --gateway-sender-id=Account_Sender_to_1"


kubectl exec  gemfire-cluster-b-locator-0 --  gfsh -e "connect --locator=gemfire-cluster-b-locator-0.gemfire-cluster-b-locator.tanzu-data-site-2.svc.cluster.local[10334]"  -e  "create region --name=health2 --type=PARTITION"

#  ------------
# Deploy Apps

kubectl apply -f deployment/cloud/k8/apps/account-service/wan-ha/account-service-cluster-a.yml --namespace=tanzu-data-site-1

# gemfire-cluster-b-locator-0.gemfire-cluster-b-locator.tanzu-data-site-2.svc.cluster.local

kubectl apply -f deployment/cloud/k8/apps/account-service/wan-ha/account-service-cluster-b.yml --namespace=tanzu-data-site-2


# Spring Cloud Gateway
kubectl create namespace tanzu-data-wan
kubectl config set-context --current --namespace=tanzu-data-wan

kubectl apply -f deployment/cloud/k8/apps/spring-gateway-healthcheck/spring-gateway-healthcheck.yml --namespace=tanzu-data-wan


sleep 5


kubectl wait pod -l=name=spring-gateway-healthcheck --for=condition=Ready --timeout=160s  --namespace=tanzu-data-wan

# -----------
kubectl apply -f deployment/cloud/k8/data-services/gemfire/gideonConsole/gemfire-management-console.yml --namespace=tanzu-data-wan


kubectl get services --namespace=tanzu-data-wan
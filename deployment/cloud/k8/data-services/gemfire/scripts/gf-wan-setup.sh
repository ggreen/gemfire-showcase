kubectl create namespace tanzu-data-wan
kubectl config set-context --current --namespace=tanzu-data-wan

kubectl exec cluster-a-locator-0 --  gfsh -e "connect --locator=cluster-a-locator-0.cluster-a-locator.tanzu-data.svc.cluster.local[10334]" -e "create gateway-receiver"  -e  "create gateway-sender --id=Account_Sender_to_2 --parallel=true  --remote-distributed-system-id=2 --enable-persistence=true --enable-batch-conflation=true"

kubectl exec cluster-a-locator-0 --  gfsh -e "connect --locator=cluster-a-locator-0.cluster-a-locator.tanzu-data.svc.cluster.local[10334]"  -e  "create region --name=Account --type=PARTITION --gateway-sender-id=Account_Sender_to_2"


kubectl exec cluster-a-locator-0 --  gfsh -e "connect --locator=cluster-a-locator-0.cluster-a-locator.tanzu-data.svc.cluster.local[10334]"  -e  "create region --name=health1 --type=PARTITION"


#------
# Cluster 2

kubectl exec  cluster-b-locator-0 --  gfsh -e "connect --locator=cluster-b-locator-0.cluster-b-locator.tanzu-data.svc.cluster.local[10334]" -e  "create gateway-receiver" -e  "create gateway-sender --id=Account_Sender_to_1 --parallel=true  --remote-distributed-system-id=1 --enable-persistence=true --enable-batch-conflation=true"


kubectl exec  cluster-b-locator-0 --  gfsh -e "connect --locator=cluster-b-locator-0.cluster-b-locator.tanzu-data.svc.cluster.local[10334]"  -e  "create region --name=Account --type=PARTITION --gateway-sender-id=Account_Sender_to_1"


kubectl exec  cluster-b-locator-0 --  gfsh -e "connect --locator=cluster-b-locator-0.cluster-b-locator.tanzu-data.svc.cluster.local[10334]"  -e  "create region --name=health2 --type=PARTITION"

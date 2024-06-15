
kubectl wait pod -l=gemfire.vmware.com/app=gemfire1-server --for=condition=Ready --timeout=30s
kubectl  exec -it gemfire1-locator-0 -- gfsh -e "connect --locator=gemfire1 -locator-0.gemfire1-locator.default.svc.cluster.local[10334]" -e "create region --name=Properties --type=PARTITION_PERSISTENT"
kubectl  exec -it gemfire1-locator-0 -- gfsh -e "connect --locator=gemfire1-locator-0.gemfire1-locator.default.svc.cluster.local[10334]" -e "create region --name=Account --type=PARTITION_PERSISTENT"

PRe-requisite


PVC Cleanup if needed
```shell
k delete GemFireCluster gemfire1 gemfire2

k delete pvc data-gemfire1-locator-0 data-gemfire1-locator-1 data-gemfire1-server-0 data-gemfire1-server-1 data-gemfire1-server-2 data-gemfire2-server-0 data-gemfire2-server-1 data-gemfire2-server-2 data-gemfire2-locator-0 data-gemfire2-locator-1
```


Install Operator

```shell
cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase
./deployments/cloud/k8/data-services/gemfire/gf-k8-setup.sh
```


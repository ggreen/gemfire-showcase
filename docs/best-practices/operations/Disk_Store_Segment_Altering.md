
```shell
./deployment/local/gemfire/start.sh
```


```shell
$GEMFIRE_HOME/bin/gfsh -e "connect"  -e "create disk-store --name=test-segment-disk --dir=."
```

```shell
$GEMFIRE_HOME/bin/gfsh  -e "connect" -e "create region --name=test-segment --type=PARTITION_PERSISTENT --disk-store=test-segment-disk"
```

```shell
$GEMFIRE_HOME/bin/gfsh  -e "connect" -e "put --region=/test-segment --key=1 --value=1"
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "put --region=/test-segment --key=1 --value=2"
```

upgrade offline-disk-store --name=test-segment-disk --max-oplog-size=20248 --destination-disk-dirs=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase/runtime/data/test-segment --source-disk-dirs=/Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-10.2.3/bin/server1-2members/test-segment-disk
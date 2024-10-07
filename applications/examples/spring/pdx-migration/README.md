
```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --name=PdxMigration --type=PARTITION"
```


```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "export data --region=PdxMigration --parallel=true --member=server1  --file=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase/deployment/scripts/gemfire-devOps-bash/runtime/PdxMigration.gfd"
```
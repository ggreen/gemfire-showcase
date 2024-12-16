

This application is an example reading from a GemFire snapshot.

You can read a snapshot entry-by-entry for further processing.
For example, you can use it to transform PDX entries to fix corruption issues.


## Example Reader
The following is an example of a snapshot reader that processes entries from a previously generated snapshot file.

See [AppConfig.java](src%2Fmain%2Fjava%2Fshowcase%2Fgemfire%2Fpdx%2Fmigration%2FAppConfig.java)


## Generating a Snapshot

Based on the following region definition

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --name=PdxMigration --type=PARTITION"
```

The following will created a snapshot

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "export data --region=PdxMigration --parallel=true --member=server1  --file=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase/deployment/scripts/gemfire-devOps-bash/runtime/PdxMigration.gfd"
```
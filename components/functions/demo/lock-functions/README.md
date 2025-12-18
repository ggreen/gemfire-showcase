

```shell
create region --name=test --type=PARTITION
```



```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "deploy --jar=$PWD/components/functions/demo/lock-functions/build/libs/lock-functions-0.0.1-SNAPSHOT.jar"
```

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "put --key=junit --value=junit --region=/test"
```

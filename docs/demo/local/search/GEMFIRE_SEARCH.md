

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create lucene index --name=accountIndex --region=/Account 
   --field=name"
```


```shell

```

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --name=Account --type=PARTITION"
```





# Running Application


```shell
java -jar applications/examples/spring/account-service/build/libs/account-service-0.0.1-SNAPSHOT.jar --spring.data.gemfire.pool.locators="localhost[10334]" --server.port=8050
```
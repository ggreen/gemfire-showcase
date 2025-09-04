# Demo Functions

Start GemFire

```shell
./deployment/local/gemfire/start-multi-servers.sh
```
Create region

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --name=test-redundant --type=PARTITION_REDUNDANT_PERSISTENT"
```

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --name=test --type=PARTITION"
```


Deploy Functions 

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "deploy --jar=$PWD/components/functions/demo/gemfire-demo-functions/build/libs/gemfire-demo-functions-0.0.1-SNAPSHOT.jar"
```

Connect with gfsh

```shell
$GEMFIRE_HOME/bin/gfsh
```

```gfsh
connect
```


Add data

```shell
put --key=1 --value=1 --region=/test-redundant
put --key=2 --value=2 --region=/test-redundant
put --key=3 --value=3 --region=/test-redundant
put --key=4 --value=4 --region=/test-redundant
put --key=5 --value=5 --region=/test-redundant
put --key=6 --value=6 --region=/test-redundant
```

Add data

```shell
put --key=1 --value=1 --region=/test
put --key=2 --value=2 --region=/test
put --key=3 --value=3 --region=/test
put --key=4 --value=4 --region=/test
put --key=5 --value=5 --region=/test
put --key=6 --value=6 --region=/test
put --key=11 --value=11 --region=/test
put --key=12 --value=12 --region=/test
put --key=13 --value=13 --region=/test
put --key=14 --value=14 --region=/test
put --key=15 --value=15 --region=/test
put --key=16 --value=16 --region=/test
put --key=21 --value=21 --region=/test
put --key=22 --value=22 --region=/test
put --key=23 --value=23 --region=/test
put --key=24 --value=24 --region=/test
put --key=25 --value=25 --region=/test
put --key=26 --value=26 --region=/test
```

```gfsh
list functions
```

-----------------

## ReadOpOnRegionFunction

```shell
execute function --id=ReadOpOnRegionFunction --region=/test-redundant --arguments=1,2,3 --filter=1,2
```

See execution on all servers
```shell
execute function --id=ReadOpOnRegionFunction --arguments=1 --region=/test
```

```shell
show log --member=server1
show log --member=server2
show log --member=server3
```

Only Execute on server with filter key=1
```shell
execute function --id=ReadOpOnRegionFunction --arguments=one --region=/test --filter=1
```

```shell
show log --member=server1
show log --member=server2
show log --member=server3
```

---------------

## WriteOpOnRegionFunction

Write execute on all servers without filter keys

```gfsh
execute function --id=WriteOpOnRegionFunction --region=/test --arguments=writeall 
```

```shell
show log --member=server1
show log --member=server2
show log --member=server3
```


Write execute on server with primary key

```gfsh
execute function --id=WriteOpOnRegionFunction --region=/test-redundant --arguments=write2 --filter=2
```

```shell
show log --member=server1
show log --member=server2
show log --member=server3
```
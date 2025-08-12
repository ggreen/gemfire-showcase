

Insert 1K records

```shell
for i in $(seq 1 1000); do
    curl -s -o /dev/null -w "Total time: %{time_total}s\n"  'http://localhost:8080/accounts' \
      -H 'accept: */*' \
      -H 'Content-Type: application/json' \
      -d "{ \"id\": \"$i\", \"name\": \"account $i\"}"  
done
```

Get Account 1
```shell
curl -X 'GET' \
  'http://localhost:8080/accounts/20' \
  -H 'accept: */*'
```

Get operations timing (Spring overhead included)

```shell
for i in $(seq 1 1000); do
  curl -s -i -o /dev/null -w "Total time: %{time_total}s\n"  -X 'GET' \
  "http://localhost:8080/accounts/$i" \
  -H 'accept: */*'
done
```

## Performance get test

This action performs region GET operations
Ex: Region Get 10,000 times of an entry of the first data entry in the region

Create region "test"

```shell
export LOCATOR_PORT=10001
$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[$LOCATOR_PORT]" -e  "put --region=test --key=1 --value=\"{id:test, name:test,fn: test, ln:test, email: test, phone:test2\""
```

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[$LOCATOR_PORT]" -e  "get --region=test --key=1"
```


Performance a get

```shell
export LOCATOR_PORT=10001
java -jar applications/gemfire-perf-test/build/libs/gemfire-perf-test-0.0.3.jar --action=get --regionName=test  --threadCount=10  --threadSleepMs=0  --loopCount=10000 --server.port=0 --spring.data.gemfire.pool.locators="localhost[$LOCATOR_PORT]"
```

Query

```oql
select * from /Account where name like '%99%'
```

Select fields
```oql
select name from /Account where name like '%99%'
```


Limit results
```oql
select name 
from /Account 
where name like '%99%'  limit 2
```

order by

```oql
select * 
from /Account 
where name like '%99%'  
order by name desc
```


and SQL

```oql
select * 
from /Account 
where id < '400' and name like '%99%'  
```

or SQL

```oql
select * 
from /Account 
where name like '%99%' or name like '%3%' 
```

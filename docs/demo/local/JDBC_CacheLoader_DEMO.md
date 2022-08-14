cp /Users/Projects/solutions/nyla/nyla-core/build/libs/nyla.solutions.core-1.5.1-SNAPSHOT.jar

cd /Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-9.15.0/bin

./gfsh




```shell
start locator --name=locator
```

```shell
configure pdx --disk-store --read-serialized=true
```
```shell
deploy --jar=/Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-9.15.0/lib/HikariCP-4.0.3.jar
deploy --jar=/Users/devtools/repositories/RDMS/PostgreSQL/driver/postgresql-42.2.9.jar
deploy --jar=/Users/Projects/solutions/nyla/nyla-core/build/libs/nyla.solutions.core-1.5.1-SNAPSHOT.jar
deploy --jar=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-extensions/components/gemfire-extenions-api/build/libs/gemfire-extensions-core-1.1.1-SNAPSHOT.jar

```



java -DCRYPTION_KEY=PIVOTAL -classpath /Users/Projects/solutions/nyla/nyla-core/build/libs/nyla.solutions.core-1.5.1-SNAPSHOT.jar nyla.solutions.core.util.Cryption $1


```shell
JDBC_LOADER_TEST_CACHELOADER_SQL=select firstName as "firstName", lastName as "lastName", loginID as "loginID" from test_cacher where email = ?
```


```shell
create region --name=Test_CacheLoader  --type=PARTITION_PERSISTENT --cache-loader=com.vmware.data.services.gemfire.integration.jdbc.JdbcCacheLoader
```


CREATE TABLE test_cacher (
firstName varchar(255),
lastName varchar(255),
loginID varchar(255),
email varchar(255)
);

insert into test_cacher(firstName, lastName, loginID,email) 
values
(
  'Josiah',
  'Imani',
  'jimani',
  'jimani@test.unit'
);


get --key="jimani@test.unit" --region=/Test_CacheLoader


query --query="select * from  /Test_CacheLoader"

remove --region=/Test_CacheLoader --key="jimani@test.unit"
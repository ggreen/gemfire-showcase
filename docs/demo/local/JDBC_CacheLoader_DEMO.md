
Build Fat JAr

```shell
cd components/gemfire-extensions-core/
gradle clean build shadowJar -x test
```

Generate password 
```shell
java -DCRYPTION_KEY=PIVOTAL -classpath deployments/libs/nyla.solutions.core-1.5.1.jar nyla.solutions.core.util.Cryption $POSTGRES_DB_PASSWORD
```

```shell
cd /Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-10.0.2/bin
```



```shell
#export CLASSPATH=/Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-9.15.0/lib/HikariCP-4.0.3.jar:/Users/devtools/repositories/RDMS/PostgreSQL/driver/postgresql-42.2.9.jar:/Users/Projects/solutions/nyla/nyla-core/build/libs/nyla.solutions.core-1.5.1.jar
export JDBC_URL=jdbc:postgresql://localhost:5432/postgres
export JDBC_DRIVER_CLASS=org.postgresql.Driver
export JDBC_USERNAME=postgres
export JDBC_PASSWORD=CRYPTED_PASSWORD_HERE
```

Start Gfsh

```shell
./gfsh
```

```shell
start locator --name=locator --locators=127.0.0.1[10334] --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1
```

```shell
configure pdx --disk-store --read-serialized=true
```

```shell
start server --name=server --J=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 --locators=127.0.0.1[10334] --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1 --start-rest-api=true --http-service-bind-address=127.0.0.1 --http-service-port=9090 --J=-DCRYPTION_KEY=PIVOTAL --J=-Dconfig.properties=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-extensions/deployments/gemfire-server/config/gf-extensions.properties
```


```shell
deploy --jar=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-extensions/components/gemfire-extensions-core/build/libs/gemfire-extensions-core-2.0.0-SNAPSHOT-all.jar
```




```shell
create region --name=user_load  --type=PARTITION --cache-loader=com.vmware.data.services.gemfire.integration.jdbc.JdbcCacheLoader
```

In Postgres

```sqlite-sql

CREATE TABLE user_profiles (
email varchar(255),
firstName varchar(255),
lastName varchar(255),
loginID varchar(255),
PRIMARY KEY (email)
);

insert into user_profiles(firstName, lastName, loginID,email) 
values
(
  'Josiah',
  'Imani',
  'jimani',
  'jimani@test.unit'
);
```


In gfsh

```shell
get --key="jimani@test.unit" --region=/user_load
```



query --query="select * from  /Test_CacheLoader"

remove --region=/Test_CacheLoader --key="jimani@test.unit"
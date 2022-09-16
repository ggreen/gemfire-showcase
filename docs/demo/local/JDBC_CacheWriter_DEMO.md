
```shell
cd /Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-9.15.0/bin
./gfsh
```

Generate password
```shell
java -DCRYPTION_KEY=PIVOTAL -classpath /Users/Projects/solutions/nyla/nyla-core/build/libs/nyla.solutions.core-1.5.1.jar nyla.solutions.core.util.Cryption $1
```

```shell
export CLASSPATH=/Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-9.15.0/lib/HikariCP-4.0.3.jar:/Users/devtools/repositories/RDMS/PostgreSQL/driver/postgresql-42.2.9.jar:/Users/Projects/solutions/nyla/nyla-core/build/libs/nyla.solutions.core-1.5.1.jar
export JDBC_URL=jdbc:postgresql://localhost:5432/postgres
export JDBC_DRIVER_CLASS=org.postgresql.Driver
export JDBC_USERNAME=postgres
export JDBC_PASSWORD=CRYPTED_PASSWORD_HERE
```



```shell
./gfsh
```

Gfsh
```shell
start locator --name=locator --locators=127.0.0.1[10334] --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1
```

```shell
configure pdx --disk-store --read-serialized=true
```

```shell
start server --name=server   --locators=127.0.0.1[10334] --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1 --start-rest-api=true --http-service-bind-address=127.0.0.1 --http-service-port=9090 --include-system-classpath=true --J=-DCRYPTION_KEY=PIVOTAL --J=-Dconfig.properties=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-extensions/deployments/gemfire-server/config/gf-extensions.properties
```


```shell
deploy --jar=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-extensions/components/gemfire-extenions-api/build/libs/gemfire-extensions-core-1.1.1-SNAPSHOT.jar
```





```shell
create region --name=user_profiles  --type=PARTITION --cache-writer=com.vmware.data.services.gemfire.integration.jdbc.JdbcCacheWriter
```

```shell
open http://localhost:9090/geode/swagger-ui.html
```

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


```shell
curl -X 'POST' \
'http://localhost:9090/geode/v1/user_profiles?keys=aimani%40pivotal.io&op=PUT' \
-H 'accept: application/json;charset=UTF-8' \
-H 'Content-Type: application/json;charset=UTF-8' \
-d '{
"firstName" : "AImani",
"lastName" : "Imani",
"loginID" :  "aimani",
"email" : "aimani@pivotal.io"
}'
```

```shell
curl -X 'PUT' \
'http://localhost:9090/geode/v1/user_profiles?keys=nimani%40pivotal.io&op=PUT' \
-H 'accept: application/json;charset=UTF-8' \
-H 'Content-Type: application/json;charset=UTF-8' \
-d '{
"firstName" : "Noles",
"lastName" : "Imani",
"loginID" :  "nimani",
"email" : "nimani@vmware.com"
}'
```

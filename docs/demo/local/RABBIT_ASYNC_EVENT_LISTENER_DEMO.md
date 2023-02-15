

Generate password
```shell
java -DCRYPTION_KEY=PIVOTAL -classpath /Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-extensions/applications/libs/nyla.solutions.core-1.5.1.jar nyla.solutions.core.util.Cryption $1
```

```shell
#export CLASSPATH="/Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-9.15.3/lib/HikariCP-4.0.3.jar:/Users/devtools/repositories/RDMS/PostgreSQL/driver/postgresql-42.2.9.jar:/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-extensions/applications/libs/nyla.solutions.core-1.5.1.jar:/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-extensions/components/gemfire-extensions-core/build/libs/gemfire-extensions-core-1.2.0.jar:/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-extensions/components/gemfire-rabbitmq/build/libs/gemfire-rabbitmq-1.0.0.jar:/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-extensions/deployments/gemfire-server/lib/gemfire-rabbitmq/amqp-client-5.16.0.jar:/Users/devtools/repositories/IMDG/gemfire/gemfire-for-redis-apps-1.0.1/lib/*"
#export JDBC_URL=jdbc:postgresql://localhost:5432/postgres
#export JDBC_DRIVER_CLASS=org.postgresql.Driver
#export JDBC_USERNAME=postgres

```

```shell
cd /Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-9.15.3/bin
./gfsh
```
```shell
start locator --name=locator1 --port=10334 --locators="127.0.0.1[10334],127.0.0.1[10434]" --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1
```

```shell
configure pdx --disk-store --read-serialized=true
```

```shell
start server --name=server1 --initial-heap=500m --max-heap=500m  --locators="127.0.0.1[10334]"  --server-port=40401 --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1 --start-rest-api=true --http-service-bind-address=127.0.0.1 --http-service-port=9090  --J=-Dgemfire-for-redis-port=6379 --J=-Dgemfire-for-redis-enabled=true --include-system-classpath=true --J=-DCRYPTION_KEY=PIVOTAL --J=-Dconfig.properties=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-extensions/deployments/gemfire-server/config/gf-extensions.properties
```


```shell
create async-event-queue --id=rabbit --parallel=true --enable-batch-conflation=true --batch-size=10 --batch-time-interval=10 --persistent=true --disk-store=DEFAULT --disk-synchronous=true --forward-expiration-destroy=true --max-queue-memory=10 --dispatcher-threads=5  --listener=com.vmware.data.solutions.rabbitmq.gemfire.RabbitAsyncEventListener

```

```shell
create region --name=customers  --type=PARTITION --cache-loader=com.vmware.data.services.gemfire.integration.jdbc.JdbcJsonPdxLoader --cache-writer=com.vmware.data.services.gemfire.integration.jdbc.JdbcJsonCacheWriter --async-event-queue-id=rabbit
```

```json
{
  "@type": "java.lang.Object",
  "firstName": "Imani",
  "lastName": "Victoria",
  "loginID": "ivictoria",
  "email": "ivictoria@vmware.com"
}
```

In Postgres

```shell
psql -d postgres -U postgres
```

```sqlite-sql
CREATE TABLE customers (
email varchar(255),
"firstName" varchar(255),
"lastName" varchar(255),
PRIMARY KEY (email)
);



insert into customers("firstName", "lastName",email)
(select r."firstName", r."lastName",email
    from json_to_record('{"firstName": "John","lastName":"Smith","email":"jsmith@vmware.com"}') 
    as r("firstName" varchar, "lastName" varchar, email text)) 
ON CONFLICT(email) DO UPDATE SET "firstName" = customers."firstName"


insert into customers("firstName", "lastName",email)
(select r."firstName", r."lastName",email
    from json_to_record('{"firstName": "Joe","lastName":"Smiths","email":"jsmith@vmware.com"}') 
    as r("firstName" varchar, "lastName" varchar, email text)) 
ON CONFLICT(email) 
DO UPDATE SET "firstName" = EXCLUDED."firstName", 
                "lastName" = EXCLUDED."lastName"
    
select row_to_json(customers)
from customers
where email = 'e1@theRevelationSquad.com'
    
```



```shell
curl -X 'PUT' \
'http://localhost:9090/geode/v1/customers?keys=jdoe%40vmware.com&op=PUT' \
-H 'accept: application/json;charset=UTF-8' \
-H 'Content-Type: application/json;charset=UTF-8' \
-d '{
"email" : "jdoe@vmware.com",
"firstName" : "Jane",
"lastName" : "Doe"
}'   
```

```shell
curl -X 'PUT' \
'http://localhost:9090/geode/v1/customers?keys=gdoe%40vmware.com&op=PUT' \
-H 'accept: application/json;charset=UTF-8' \
-H 'Content-Type: application/json;charset=UTF-8' \
-d '{
"email" : "gdoe@vmware.com",
"firstName" : "Gill",
"lastName" : "Doe"
}'   
```

```shell
curl -X 'PUT' \
'http://localhost:9090/geode/v1/customers?keys=jsmith%40vmware.com&op=PUT' \
-H 'accept: application/json;charset=UTF-8' \
-H 'Content-Type: application/json;charset=UTF-8' \
-d '{
"email" : "jsmith@vmware.com",
"firstName" : "John",
"lastName" : "Smith"
}'   
```

```shell
curl -X 'PUT' \
'http://localhost:9090/geode/v1/customers?keys=msmith%40vmware.com&op=PUT' \
-H 'accept: application/json;charset=UTF-8' \
-H 'Content-Type: application/json;charset=UTF-8' \
-d '{
"email" : "msmith@vmware.com",
"firstName" : "Mary",
"lastName" : "Smith"
}'   
```

In gfsh

http://localhost:9090/geode/swagger-ui/index.html


```shell
curl -X 'GET' \
  'http://localhost:9090/geode/v1/customers?limit=50&keys=jdoe@vmware.com' \
  -H 'accept: application/json;charset=UTF-8'
```



query --query="select * from  /customers where lastName like  'S%'"



Got http://localhost:9090/geode/swagger-ui/index.html#/queries/runAdhocQuery
select * from  /customers where lastName like  'S%25'
or run
```shell
curl -X 'GET' \
  'http://localhost:9090/geode/v1/queries/adhoc?q=select%20%2A%20from%20%20%2Fcustomers%20where%20lastName%20like%20%20%27S%2525%27' \
  -H 'accept: application/json;charset=UTF-8'
```



------------------------


```shell
start server --name=server2 --initial-heap=500m --max-heap=500m  --locators="127.0.0.1[10334],127.0.0.1[10434]"  --server-port=40402 --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1 --start-rest-api=true --http-service-bind-address=127.0.0.1 --http-service-port=9092  --J=-Dgemfire-for-redis-port=6372 --J=-Dgemfire-for-redis-enabled=true --include-system-classpath=true --J=-DCRYPTION_KEY=PIVOTAL --J=-Dconfig.properties=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-extensions/deployments/gemfire-server/config/gf-extensions.properties

```

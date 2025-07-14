#  Account Service


Sample Spring REST API GemFire client application 

# Start GemFire

```shell
start locator --name=locator
```

```shell
configure pdx --read-serialized=true --disk-store
```

```shell
start server --name=server
```

```shell
create region --name=Account --type=PARTITION
```


# Building

export MAVEN_OPTS="-Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true"


```shell
mvn package
```

# Running Application


```shell
java -jar applications/examples/spring/account-service/build/libs/account-service-0.0.1-SNAPSHOT.jar --spring.data.gemfire.pool.locators="localhost[10334]" --server.port=8050
```

```shell
open http://localhost:8050
```


## Load Data

```shell
for i in {1..300}
do
  accountJson='{ "id": "';
  accountJson+=$i;
  accountJson+='", "name": "Account ';
  accountJson+=$i;
  accountJson+='"}';

  curl -X 'POST' \
  'http://localhost:8050/accounts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d $accountJson
  echo;  
done
```

Get Record

```shell
curl -X 'GET' \
  'http://localhost:8050/accounts/1' \
  -H 'accept: */*'
```

# Docker building image

```shell
gradle publishMavenPublicationToMavenLocalRepository
cd applications/examples/spring/account-service
gradle build

docker build   --platform linux/amd64,linux/arm64 -t  cloudnativedata/account-service-gemfire-showcase:0.0.1-SNAPSHOT .
#docker build   --platform linux/amd64,linux/arm64 --build-arg JAR_FILE=build/libs/\*.jar -t userAccount-service:0.0.1-SNAPSHOT .
#docker build  --platform linux/amd64,linux/arm64 -t userAccount-service-gemfire-showcase:0.0.1-SNAPSHOT .

```

```shell
docker tag  cloudnativedata/account-service-gemfire-showcase:0.0.1-SNAPSHOT cloudnativedata/ cloudnativedata/account-service-gemfire-showcase:0.0.1-SNAPSHOT
docker push cloudnativedata/ cloudnativedata/account-service-gemfire-showcase:0.0.1-SNAPSHOT
```

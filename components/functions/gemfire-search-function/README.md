# Clear Remove Function


This function test a GemFire search region data in a GemFire partitioned region.
This function has been tested with the following

- GemFire version 10.1.0 
- Java 17
- Gradle 8.4

## Build Jar

Set GemFire Maven Repository user credentials as environment variables.
See https://gemfire.dev/quickstart/java/

```shell
export PIVOTAL_MAVEN_USERNAME=$HARBOR_USER
export PIVOTAL_MAVEN_PASSWORD=$HARBOR_PASSWORD
```

Change directory to components from the root project directory

```shell
cd components/functions/gemfire-search-function
```

Perform a Grade build

```shell
gradle build 
```

-------------------
# Testing Function

## Start GemFire

```shell
./deployment/local/gemfire/start.sh
```

You must deploy the function using the gfsh deploy command.

Example:

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "deploy --jar=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase/components/functions/gemfire-search-function/build/libs/gemfire-search-function-1.0.0-SNAPSHOT.jar"
```

```shell
cd $GEMFIRE_HOME/bin
./gfsh 
```

```shell
java -jar applications/gemfire-rest-app/build/libs/gemfire-rest-app-0.0.1-SNAPSHOT.jar
```

```json
{
  "@type": "java.util.HashMap",
  "firstName" :  "John",
  "lastName" :  "Doe",
  "email" : "jdoe@jdoe.jdoe", 
  "contacts" : {
    "phoneNumbers" : ["555-555-5555", "111-111-1111"],
    "address" : "1 Straight street",
    "cityTown" : "JC",
    "stateProvince" : "NJ",
    "zip" : "55555",
    "country" : "US"
  }
}

```


```shell
curl -X 'POST' \
  'http://localhost:8080/region/example-search-region/1' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "@type": "java.util.HashMap",
  "firstName" :  "John",
  "lastName" :  "Doe",
  "email" : "jdoe@jdoe.jdoe", 
  "contacts" : {
    "phoneNumbers" : ["555-555-5555", "111-111-1111"],
    "address" : "1 Straight street",
    "cityTown" : "JC",
    "stateProvince" : "NJ",
    "zip" : "55555",
    "country" : "US"
  }
}'
```


```shell
curl -X 'POST' \
  'http://localhost:8080/region/example-search-region/2' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "@type": "java.util.HashMap",
  "firstName" :  "Jill",
  "lastName" :  "Doe",
  "email" : "jdoe@jdoe.jdoe", 
  "contacts" : {
    "phoneNumbers" : ["222-222-2222", "333-333-3333"],
    "address" : "2 Straight street",
    "cityTown" : "JC",
    "stateProvince" : "NJ",
    "zip" : "55551",
    "country" : "US"
  }
}'
```

The following is an example of how to execute the function on the given a region in Gfsh.

            id = argsStrings[0];
            query = argsStrings[1];
            regionName = argsStrings[2];
            indexName = argsStrings[3];
            defaultField = argsStrings[4];
            String limitText = argsStrings[5];

```shell
execute function --id=LuceneSearchFunction --region=/example-search-region --arguments="user1,firstName:nope~ OR lastName:D~,example-search-region,simpleIndex,firstName,100"
```

Get data values should be null

```shell
get --key=1 --region=/TestClear
get --key=2 --region=/TestClear
```

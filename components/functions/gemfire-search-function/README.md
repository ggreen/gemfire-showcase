# GemFire Search Function


This is an example function that uses [GemFire Search](https://docs.vmware.com/en/VMware-GemFire-Search/1.1/gemfire-search/search_landing.html) capabilities. The function uses [PageableLuceneQueryResults](https://gemfire.dev/api/extensions/search/1.1/org/apache/geode/cache/lucene/PageableLuceneQueryResults).
It saves the results to a paging region.

**NOTE:** This server-side function requires additional memory storage to save search results to a region.
To reduce storage requirements, an alternative (preferred) approach would be to use the PageableLuceneQueryResults on the java client. 


The function should be executed using the [FunctionService.onServer(...)](https://gemfire.dev/api/gemfire/10.1/org/apache/geode/cache/execute/FunctionService) method.
Note that executing this function on multiple servers produces duplicate results, because the GemFire Search results are 
not unique to the local member. Uses the FunctionService onServer executes this function on a single member based a pool or region.

The format of the result uses the LuceneResultStruct object with the following definition.

```java
public interface LuceneResultStruct<K, V> {
    K getKey();

    V getValue();

    float getScore();
}
```

The results of the search are stored in a region named **Paging**.


The following are example input arguments in a list of strings

- id = arguments[0];
- indexName = arguments[1];
- defaultField = arguments[2]; 
- query = arguments[3]; 
- limit = arguments[4]
- pageSize = arguments[5] // default 100
- keysOnly= arguments[6] //default false



Note: Arguments in gfsh are separated by ","

Example Search in gfsh

- Search by first and last name with a limit 100 each have pageSize 1

```shell
execute function --id=GemFireSearchFunction --region=/example-search-region --arguments='user1,simpleIndex,firstName,firstName:nope~ OR lastName:D~,100,1'
```

Note the function always returns the first number of records based on page size.


This function has been tested with the following

- GemFire version 10.1.0 
- Java 17
- Gradle 8.4

## Build Jar

Set GemFire Maven Repository user credentials as environment variables.
See https://gemfire.dev/quickstart/java/


```shell
export BROADCOM_MAVEN_USERNAME=$HARBOR_USER
export BROADCOM_MAVEN_PASSWORD=$HARBOR_PASSWORD
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
./deployment/local/gemfire/start-multi-servers.sh
```

You must deploy the function using the gfsh deploy command.

Example:

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "deploy --jar=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase/components/functions/gemfire-search-function/build/libs/gemfire-search-function-1.0.0-SNAPSHOT.jar"
```

Start a generic GemFire Rest application to load JSON data into a region.

```shell
java -jar applications/gemfire-rest-app/build/libs/gemfire-rest-app-0.0.1-SNAPSHOT.jar
```


Load JSON
```shell
curl -X 'POST' \
  'http://localhost:8080/region/example-search-region/1' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "@type": "java.lang.Object",
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
}';

curl -X 'POST' \
  'http://localhost:8080/region/example-search-region/2' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "@type": "java.lang.Object",
  "firstName" :  "John2",
  "lastName" :  "Doe2",
  "email" : "jdoe2@jdoe.jdoe", 
  "contacts" : {
    "phoneNumbers" : ["555-555-5555", "111-111-1111"],
    "address" : "1 Straight street",
    "cityTown" : "JC",
    "stateProvince" : "NJ",
    "zip" : "55555",
    "country" : "US"
  }
}';

curl -X 'POST' \
  'http://localhost:8080/region/example-search-region/3' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "@type": "java.lang.Object",
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
}';


curl -X 'POST' \
  'http://localhost:8080/region/example-search-region/4' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "@type": "java.lang.Object",
  "firstName" :  "Jill1",
  "lastName" :  "Doe2",
  "email" : "jdoe2@jdoe.jdoe", 
  "contacts" : {
    "phoneNumbers" : ["222-222-2222", "333-333-3333"],
    "address" : "2 Straight street",
    "cityTown" : "JC",
    "stateProvince" : "NJ",
    "zip" : "55551",
    "country" : "US"
  }
}';
```

The following is an example of how to execute the function on the given region in Gfsh.

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "execute function --id=GemFireSearchFunction --member=server1 --arguments='user1,example-search-region,simpleIndex,firstName,firstName:nope~ OR lastName:D~,100'"
```

Get Keys Only
```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "execute function --id=GemFireSearchFunction --member=server2 --arguments='user1,example-search-region,simpleIndex,firstName,firstName:nope~ OR lastName:D~,100,10,true'"
```

Testing paging


```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "execute function --id=GemFireSearchFunction --member=server1 --arguments='user1,example-search-region,simpleIndex,firstName,firstName:nope~ OR lastName:D~,100,1'"
```


Get First Page

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "get --key=user1-1 --region=/Paging"
```

Get Second Page

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "get --key=user1-2 --region=/Paging"
```
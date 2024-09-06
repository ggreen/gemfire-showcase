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

The following is an example of how to execute the function on the given a region in Gfsh.

```shell
execute function --id=ClearRegionRemoveAllFunction --region=/TestClear
```

Get data values should be null

```shell
get --key=1 --region=/TestClear
get --key=2 --region=/TestClear
```

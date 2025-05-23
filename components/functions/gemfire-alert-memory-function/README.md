# Clear Remove Function


This function allows you to clear region data in a GemFire partitioned region.
This function has been tested with the following

- GemFire version 10.1.0 
- Java 17
- Gradle 8.4

## Build Jar

Set Pivotal Maven Repository user credentials as environment variables.
See https://gemfire.dev/quickstart/java/

```shell
export BROADCOM_MAVEN_USERNAME=$HARBOR_USER
export BROADCOM_GEMFIRE_MAVEN_PASSWORD=$HARBOR_PASSWORD
```

Change directory to components from the root project directory

```shell
cd components/functions/gemfire-alert-memory-function
```

Perform a Grade build

```shell
gradle build 
```

-------------------
# Testing Function

## Start GemFire

See script 
```shell
./deployment/local/gemfire/start-multi-servers.sh
```

You must deploy the function using the gfsh deploy command.

Example:

```shell
deploy --jar=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase/components/functions/gemfire-alert-memory-function/build/libs/gemfire-alert-memory-function-0.0.1-SNAPSHOT.jar
```


The following is an example of how to execute the function on the given a region in Gfsh.

To check if memory above 50%

```shell
execute function --id=AlertMemoryFunction --arguments=50
```

Get data values should be null

```shell
get --key=1 --region=/TestClear
get --key=2 --region=/TestClear
```

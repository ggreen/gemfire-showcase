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
export BROADCOM_MAVEN_PASSWORD=$HARBOR_PASSWORD
```

Change directory to components from the root project directory

```shell
cd components/functions/gemfire-clear-region-function
```

Perform a Grade build

```shell
gradle build 
```

-------------------
# Testing Function

## Start GemFire

```shell
cd $GEMFIRE_HOME/bin
```

Start Gfsh

```shell
./gfsh
```

Start Locator in Gfsh
```shell
start locator --name=locator
```

Configure Pdx
```shell
configure pdx --read-serialized=true --disk-store
```

Start Cache Server in gfsh
```shell
start server --name=server1 --locators=localhost[10334]
```

You must deploy the function using the gfsh deploy command.

Example:

```shell
deploy --jar=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-extensions/components/functions/gemfire-clear-region-function/build/libs/gemfire-touch-function-1.0.0-SNAPSHOT.jar
```

Create a region in gfsh

```shell
create region --name=TestClear --type=PARTITION
```

Add data

```shell
put --key=1 --value=1 --region=/TestClear
put --key=2 --value=2 --region=/TestClear
```

Get data 
```shell
get --key=1 --region=/TestClear
get --key=2 --region=/TestClear
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

# Delete Functions


This function allows you to delete region data in a GemFire region based on a query of keys.
This function has been tested with the following

- GemFire version 10.1.0 
- Java 17
- Gradle 8.4

## Build Jar

Set Pivotal Maven Repository user credentials as environment variables.
See https://gemfire.dev/quickstart/java/

```shell
export PIVOTAL_MAVEN_USERNAME=$HARBOR_USER
export PIVOTAL_MAVEN_PASSWORD=$HARBOR_PASSWORD
```

Change directory to components from the root project directory

```shell
cd components/functions/gemfire-delete-region-function
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
deploy --jar=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase/components/functions/gemfire-delete-region-function/build/libs/gemfire-delete-region-function-1.0.0-SNAPSHOT.jar
```

Create a region in gfsh

```shell
create region --name=TestClear --type=PARTITION
```

Add data

```shell
put --key=1 --value=1 --region=/TestClear
put --key=2 --value=2 --region=/TestClear
put --key=3 --value=3 --region=/TestClear
put --key=hello1 --value=hello1 --region=/TestClear
put --key=hello2 --value=hello2 --region=/TestClear
put --key=hello3 --value=hello3 --region=/TestClear
```

Get data 
```shell
get --key=1 --region=/TestClear
get --key=2 --region=/TestClear
```

```shell
query --query="select key from /TestClear.entries "
```


The following is an example of how to execute the function on the given a region in Gfsh.

Delete key based on contains method
```shell
execute function --id=DeleteFunction --region=/TestClear --arguments="select key from /TestClear.entries where key.contains('hello')"
```


Note: Commas in OQL must be encoded with {COMMA}
```shell
execute function --id=DeleteFunction --region=/TestClear --arguments="select key from /TestClear.entries where value in SET ('1'{COMMA}'2')"
```

Get data values 1 records with key=3

```shell
query --query="select key from /TestClear.entries "
```

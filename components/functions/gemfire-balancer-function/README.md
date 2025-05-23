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
cd components/functions/gemfire-balancer-function
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
deploy --jar=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-showcase/components/functions/gemfire-balancer-function/build/libs/gemfire-balancer-function-0.0.1-SNAPSHOT.jar
```

Create a region in gfsh

```shell
create region --name=TestBalance --type=PARTITION
```

Add data

```shell
put --key=1 --value=1 --region=/TestBalance
put --key=01 --value=01 --region=/TestBalance
put --key=02 --value=02 --region=/TestBalance
put --key=03 --value=03 --region=/TestBalance
put --key=2 --value=2 --region=/TestBalance
put --key=3 --value=3 --region=/TestBalance
put --key=11 --value=11 --region=/TestBalance
put --key=22 --value=22 --region=/TestBalance
put --key=33 --value=33 --region=/TestBalance
put --key=11 --value=11 --region=/TestBalance
put --key=22 --value=22 --region=/TestBalance
put --key=33 --value=33 --region=/TestBalance
```


The following is an example of how to execute the function on the given a region in Gfsh.

```shell
execute function --id=BalancerFunction --member=server1 --arguments=2
```


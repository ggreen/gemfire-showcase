# GemFire locking

In GemFire, the Distributed Lock Service is a specialized
tool used to coordinate access to shared resources across a distributed cluster. Unlike standard Java locks that only work within a single JVM, GemFire’s locking service ensures that a specific "named lock" can be held by only one thread across the entire distributed system.

# Code


Sample Code

```java
    var lockService =DistributedLockService.getServiceNamed(serviceName);
        if(lockService ==null)
                lockService = DistributedLockService.create(serviceName, cache.getDistributedSystem());
        logger.info("Got lock service: {}", serviceName);


        try {
            var lockAcquired = lockService.lock(lockName, -1, -1);

            //process

        } catch (InterruptedException e) {
            rfc.getResultSender().lastResult(false);
            throw new FunctionException(e);
        }
        finally {
            if (lockService.isHeldByCurrentThread(lockName)){
                try {
                    lockService.unlock(lockName);
                } catch (Exception unlockEx) {
                    logger.warn("Failed to release lock", unlockEx);
                }
            }
        }
```

Also [LockingFunction.java](src/main/java/showcase/gemfire/demo/functions/locking/LockingFunction.java)
## Setup

Create example region
```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --name=test --type=PARTITION"
```

Deploy Function

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "deploy --jar=$PWD/components/functions/demo/lock-functions/build/libs/lock-functions-0.0.1-SNAPSHOT.jar"
```

List Functions

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "list functions"
```

test in Gfsh

Put Data in function

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "put --key=junit --value=junit --region=/test"
```


```shell
execute function --id=LockFunction  --filter=junit --region=test --arguments=myLockService,-1,-1
```


---------------

# Global Region Locking


Create example region
```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --name=global --scope=GLOBAL --type=REPLICATE"
```


```shell
execute function --id=GlobalRegionLockFunction  --filter=junit --region=global --arguments=LOCK
```


## Test 


See Junit Test 

Execute two test in parallel to test the locking

Also see
[LockingFunctionTest.java](src/test/java/showcase/gemfire/demo/functions/locking/LockingFunctionTest.java)

See 
[FunctionWaitTest.java](src/test/java/showcase/gemfire/demo/functions/locking/FunctionWaitTest.java)


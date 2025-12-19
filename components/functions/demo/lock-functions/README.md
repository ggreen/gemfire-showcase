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


# Locking Using Semaphores

## AcquireSemaphoreFunction

The Acquire semaphore function uses Java Semaphore. 
The Semaphore will be acquired from a given lock with a given time out.
If another process has already acquired the semaphore for the given key,
the function will block based on the timeout.
The function must be executed on a region. A partition region 
with no persistence nor redundancy is the preferred region type

Input Arguments String Array

- [0] The number of permits is passed in as an argument. 
- [1] The timeOut number
- [2] The time Unit (NANOSECONDS,MICROSECONDS,MILLISECONDS,SECONDS,MINUTES,HOURS,DAYS)

## ReleaseSemaphoreFunction

This function works in conjunction with the AcquireSemaphoreFunction.
It will release a lock for a given filter key.
The function must be executed on a region. A partition region
with no persistence nor redundancy is the preferred region type.


Test in Gfsh

First call will succeed

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "execute function --id=AcquireSemaphoreFunction  --filter=junit --region=test --arguments=1,999,MINUTES"
```


Second will block based for timeout duration


```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "execute function --id=AcquireSemaphoreFunction  --filter=junit --region=test --arguments=1,999,MINUTES"
```

Release lock in separate shell

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "execute function --id=ReleaseSemaphoreFunction  --filter=junit --region=test"
```

---------------

## Test 


See Junit Test 

Execute two test in parallel to test the locking

Also see
[LockingFunctionTest.java](src/test/java/showcase/gemfire/demo/functions/locking/LockingFunctionTest.java)

See 
[FunctionWaitTest.java](src/test/java/showcase/gemfire/demo/functions/locking/FunctionWaitTest.java)


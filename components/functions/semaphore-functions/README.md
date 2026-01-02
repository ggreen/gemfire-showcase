# Locking Using Semaphores

This is a reference implementation using GemFire functions with a server-side semaphore.

## AcquireSemaphoreFunction

The Acquire semaphore function uses Java Semaphore. 
The Semaphore will be acquired from a given lock with a given time out.
If another process has already acquired the semaphore for the given key,
the function will block based on the timeout.

### Locking Region
The function must be executed on a region. A partition region 
with no persistence nor redundancy is the preferred region type.
This allows consistency locking for client application.
You should use a separate region to store the keys and semaphore.
This will eliminate the possibility of colliding on Keys and accidentally 
removing Semaphores.


### Input Arguments String Array

The function has the following arguments.

- [0] The number of permits is passed in as an argument (see [Semaphora Permits](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Semaphore.html#Semaphore-int-))
- [1] The timeOut value 
- [2] The timeout unit (NANOSECONDS,MICROSECONDS,MILLISECONDS,SECONDS,MINUTES,HOURS,DAYS) (See [TimeUnit](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html))

## ReleaseSemaphoreFunction

This function works in conjunction with the AcquireSemaphoreFunction.
It will release a lock for a given filter key.
The function must be executed on a region. A partition region
with no persistence nor redundancy is the preferred region type.


# Automated Semaphore Cleanup with GemFire Evictions

There may be cases where the Semaphores are not cleaned up due to client side errors that result in the ReleaseSemaphoreFunction function not being called.
It is recommended to use [GemFire Expirations](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire/10-1/gf/developing-expiration-how_expiration_works.html)
on the locking region. Expirations can set a Time to live (TTL) for all Semaphores region entries. Most operations that require a lock should complete in seconds.
In the case where there are semaphores that are not released within an expected timeframe, then you can define a TTL on the locking region entries.


# Setup

Create a dedicated region for managing semaphores (use Partitioned regions, without persistence)

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --name=locking --type=PARTITION --enable-statistics --entry-idle-time-expiration=60"
```


Deploy Functions

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "deploy --jar=$PWD/components/functions/demo/lock-functions/build/libs/lock-functions-0.0.1-SNAPSHOT.jar"
```


List Functions (optional)


```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "list functions"
```


## Testing 

Test in Gfsh

First call will succeed

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "execute function --id=AcquireSemaphoreFunction  --filter=myKey --region=locking --arguments=1,999,MINUTES"
```

Second will block based for timeout duration


```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "execute function --id=AcquireSemaphoreFunction  --filter=myKey --region=locking --arguments=1,999,MINUTES"
```

Release lock in separate shell

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "execute function --id=ReleaseSemaphoreFunction  --filter=myKey --region=locking"
```


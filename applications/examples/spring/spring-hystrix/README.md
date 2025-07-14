# spring-hystrix

This project is an example reference application to showcase using [Hystrix](https://github.com/Netflix/Hystrix) with a Spring based GemFire client.


## Getting Started

Running the application.

```bash
java -jar applications/examples/spring/spring-hystrix/build/libs/spring-hystrix-0.0.1-SNAPSHOT.jar --spring.data.gemfire.pool.default.locators="localhost[10334]" --spring.data.gemfire.pool.default.read-timeout=10000 --hystrix.timeout.ms=1 --spring.data.gemfire.pool.default.retry-attempts=1 --spring.data.gemfire.pool.max-connections=1 --hystrix.core.size=2 --app.batch.size=10000  --app.delay.ms=10 --app.loopCount=9999999
```


Properties

| Property                                        | Notes                                                                                                                                                              |
|-------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| spring.data.gemfire.pool.default.locators       | GemFire Locators to connect (ex: localhost[10334]                                                                                                                  |
| spring.data.gemfire.pool.default.read-timeout   | Configures the number of milliseconds to wait for a response from a server before timing out the operation and trying another server default (10 seconds)          |
| spring.data.gemfire.pool.default.retry-attempts | Configures the number of times to retry a request after timeout/exception. The default -1 which is all servers                                                     |
| spring.data.gemfire.pool.max-connections        | The max number of client to server connections that the pool will create. The default value of -1 means not maximum connections                                    |                                   |
| hystrix.region.name                             | region to use (default: test)                                                                                                                                      |
| hystrix.timeout.ms                              | The GemFire operation timeout                                                                                                                                      |
| hystrix.core.size                               | The number of expected host cores/vcpu for connection pooling                                                                                                      |
| app.delay.ms                                    | The delay in milliseconds to sleep between batch operations                                                                                                        |
| app.batch.size                                  | The number of the times to executed GemFire put/get operations (default 100). Note each execution will generate a new entry (may results in out of memory errors). |
| app.loopCount                                   | Number of batch executed (default 100)                                                                                                                             |

# Hystrix Timeout/Circuit Breaker GemFire Tips
 
**WARNING: Hystrix is no longer in active development, and is currently in maintenance mode.**
Considering using alternatives such as [Resilience4j](https://resilience4j.readme.io/docs/getting-started)


- HystrixCommandProperties.ExecutionIsolationStrategy.THREAD is required to use a execution timeout
  - Hystrix semaphores timeouts cannot be terminated automatically [see StackOverflow thread](https://stackoverflow.com/questions/35076743/difference-between-thread-isolated-and-semaphore-isolated-calls) 
- Tune the [Circuit Breaker Thread Pool](https://github.com/Netflix/Hystrix/wiki/Configuration?source=post_page-----c7eebb5b0ddc---------------------------------------#threadpool-properties) based on the number of cores/vcpu on the GemFire client application host machine
- Set a maximum range of connections to 1-2 times to number of cache servers. For example, if there are 10 cache servers then the range should be 10-20 maximum connections.
  - Limiting the max number of the GemFire connections. This is key when using a circuit breaker pattern (Note the default is no maximum connections). 
  - Too many connections from clients can overload servers (each server handles client threads and buffers). 
  - Monitor server thread pools and connection queues (gemfire.stats, JMX, Pulse, etc.).
- Limit the GemFire connection PoolRetryAttempts to a range of 1 to 3 attempts.
    - Note the default -1 indicates that a request should be tried against every available server before failing.
    - Limiting the number of retries will limit the number of server connections
- Optional Reduce the GemFire read timeout. The default GemFire read timeout is 10 seconds. This is expectable in most cases. Based on testing, the GemFire readout timeout should be greater than the Circuit breaker timeout in order to prevent false positives.
  - Keep the default value if you are not sure what GemFire read timeout value to use  
  - Note GemFire also has a PoolSocketConnectTimeout. This sets the number of milli-seconds specified as socket
    timeout when the client connects to the servers/locators. The default is 59000 ml or 59 seconds that may need to be tuned. The connection will then block until established or an error occurs.
# gemfire-health-check

This is a reference implementation of Health-check/repair GemFire app.
The application using Spring Boot. It uses JMX to communicate with a GemFire locator.


![check-health-architecture.png](docs/imgs/check-health-architecture.png)

It implements a health-check operation such as check if a GemFire members memory level are less than a given theshold. The checks a executed based on a schedule. It performs action such as rebalance data when a low memory or similar errors are encountered.

Note: The application is automated rebalance safety conventions to maintain a healthier state for the cluster.
For example, it will not execute a rebalance if the cluster does have a majority of members.


# Get Started

Running the health check application

```shell
java -jar applications/gemfire-health-check/build/libs/gemfire-health-check-0.0.1-SNAPSHOT.jar --gemfire.jmx.locator.url="service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi" --gemfire.check.schedule.cron="0 * * * * *" --gemfire.security.username=admin --gemfire.security.password=admin
```


Argument/Properties


| Property                                  | Notes                                                                                                                               | Default     |
|-------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|-------------|
| gemfire.jmx.locator.url                   | JMX connection URL of the GemFire locator                                                                                           |             | 
| gemfire.security.username                 | GemFire security user name if security manager is implemented                                                                       |
| gemfire.security.password                 | GemFire security password if security manager is implemented                                                                        |
| gemfire.rebalance.threshold.members.count |                                                                                                                                     |             | 
| gemfire.check.schedule.cron               | See [Spring Cron Express](https://docs.spring.io/spring-framework/reference/integration/scheduling.html#scheduling-cron-expression) | 0 * * * * * |




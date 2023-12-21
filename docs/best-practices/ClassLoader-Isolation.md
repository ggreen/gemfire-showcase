# Best Practices - GemFire ClassLoader Loader Isolation 


GemFire members are started with classloader loader isolation enabled by default. The details of classloader isolation can found here: https://docs.vmware.com/en/VMware-GemFire/10.0/gf/configuring-cluster_config-classloader_isolation.html


## Best Practices

Use Java build tools like Gradle’s shadowJar or Maven Shade plugin to build a Java Jar with the needed dependencies included
Minimize the dependencies to the essentials needed for needed components
Verify included dependendences created in the Jar(here is an example to verify the Jar dependencies using gradle: gradle dependencies --configuration runtimeClasspath)
Add any needed manifest attribute required by include dependencies such as multi-java release attribute “Multi-Release” : true (see below)


```groovy
jar {
    manifest {
    attributes 'Multi-Release' : 'true'
    }
}
```


## Known Issues

- Exclude including the dependencies: org.apache.logging.log4j, org.slf4j and com.fasterxml.jackson


This readme provides an example a JDBC Cache Loader that using classloader isolations.

https://github.com/ggreen/gemfire-extensions/blob/main/docs/demo/local/JDBC_CacheLoader_DEMO.md


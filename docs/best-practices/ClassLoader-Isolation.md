# Best Practices - GemFire ClassLoader Loader Isolation 


GemFire members are started with classloader loader isolation enabled by default. The details of classloader isolation can found here: https://docs.vmware.com/en/VMware-GemFire/10.0/gf/configuring-cluster_config-classloader_isolation.html


## Best Practices

- Use Java build tools like [Gradle’s shadowJar](https://imperceptiblethoughts.com/shadow/) or [Maven Shade](https://maven.apache.org/plugins/maven-shade-plugin/) plugin to build a Java Jar with the needed dependencies included
- Use compile only option to exclude core GemFire classes (ex: org.apache.geode.* or group: 'com.vmware.gemfire')
- Minimize the dependencies to the essentials needed for needed components
- Verify included dependencies created in the Jar(here is an example to verify the Jar dependencies using gradle: gradle dependencies --configuration runtimeClasspath)
[Add any needed manifest attribute](https://imperceptiblethoughts.com/shadow/configuration/#configuring-the-jar-manifest) required by included dependencies such as multi-java release attribute “Multi-Release” : true (see below)


```groovy
shadowJar {
    manifest {
        attributes 'Multi-Release' : 'true'
    }
}
```


## Known Issues

Exclude the following dependencies: 
- org.apache.logging.log4j
- org.slf4j
- com.fasterxml.jackson

Example to exclude these dependencies

```groovy
dependencies {
    compileOnly 'org.apache.logging.log4j:log4j-api:2.20.0'
    compileOnly 'com.fasterxml.jackson.core:jackson-databind:2.14.1'
    compileOnly 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.1'
    implementation("com.zaxxer:HikariCP:4.0.3")
            {
                exclude group: "org.slf4j"
            }
    //.....
}
```

This readme provides an example  JDBC Cache Loader that uses classloader isolations.

https://github.com/ggreen/gemfire-extensions/blob/main/docs/demo/local/JDBC_CacheLoader_DEMO.md


package io.spring.gemfire.perftest.components.serialization.dataSerialize;

import io.spring.gemfire.perftest.components.serialization.dataSerialize.domain.ExampleDataSerializable;
import nyla.solutions.core.operations.performance.BenchMarker;
import nyla.solutions.core.operations.performance.PerformanceCheck;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.PdxSerializer;

/*
    mean ms	0.4305216
    min ms	0.171167
    max ms	4.466583
    70th ms	0.352458
    90th ms	0.639667
    99.9th ms	4.466583
    99.999th ms	4.466583
    stddev ms	0.5954922624511253
    stddev ms	0.7713722889423417
 */
public class DataSerializationQA {

    private static Long loopCount = 1000000L;
    private static int threadCount = 1;
    private static Long threadLifeTimeSeconds =2L;
    private static int rampUPSeconds = 1;
    private static long threadSleepMs =1;
    private static String locatorHost = "127.0.0.1";
    private static int locatorPort = 10334;
    private static PdxSerializer pdxSerializer;
    private static String pattern = ".*";
    private static String regionName = "test";
    private static int capacity = 100;
    private static int key;
    private static ExampleDataSerializable value = JavaBeanGeneratorCreator.of(ExampleDataSerializable.class).create();

    public static void main(String[] args) throws InterruptedException {


        var cache = new ClientCacheFactory().addPoolLocator(
                locatorHost,
                locatorPort)
                .create();

        Region<Integer, ExampleDataSerializable> region = (Region)cache.createClientRegionFactory(ClientRegionShortcut.PROXY)
                .create(regionName);

        BenchMarker benchMarker = BenchMarker.builder()
                .loopCount(loopCount)
                .threadCount(threadCount)
                .threadLifeTimeSeconds(threadLifeTimeSeconds)
                .rampUPSeconds(rampUPSeconds)
                .threadSleepMs(threadSleepMs)
                .build();


        var perfCheck = new PerformanceCheck(benchMarker,capacity);


      perfCheck.perfCheck(() ->{
          value.setId(key);
          region.put(key++,value);
      });

        System.out.println(perfCheck.getReport());
    }
}

package io.spring.gemfire.perftest.components.serialization.pdx;

import io.spring.gemfire.perftest.components.serialization.pdx.domain.PdxData;
import nyla.solutions.core.operations.performance.BenchMarker;
import nyla.solutions.core.operations.performance.PerformanceCheck;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

/*
    mean ms	0.19895787
    min ms	0.124042
    max ms	0.623708
    70th ms	0.203375
    90th ms	0.254459
    99.9th ms	0.623708
    99.999th ms	0.623708
    stddev ms	0.07525987375252428
 */
public class PdxReflectionQA {

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
    private static PdxData value = JavaBeanGeneratorCreator.of(PdxData.class).create();

    public static void main(String[] args) throws InterruptedException {

        pdxSerializer = new ReflectionBasedAutoSerializer(".*");

        var cache = new ClientCacheFactory().addPoolLocator(
                locatorHost,
                locatorPort)
                .setPdxSerializer(pdxSerializer)
                .create();

        Region<Integer, PdxData> region = (Region)cache.createClientRegionFactory(ClientRegionShortcut.PROXY)
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

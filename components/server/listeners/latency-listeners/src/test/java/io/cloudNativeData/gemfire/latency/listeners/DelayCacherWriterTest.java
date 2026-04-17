package io.cloudNativeData.gemfire.latency.listeners;

import nyla.solutions.core.patterns.workthread.Boss;
import org.apache.geode.cache.client.AllConnectionsInUseException;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.jupiter.api.Test;

import static java.lang.System.out;

/**
 *
 * static final int DEFAULT_READ_TIMEOUT
 * The default amount of time, in milliseconds, to wait for a response from a server
 * Current value: 10000.
 */

class DelayCacherWriterTest {

    private final static String host = "localhost";
    private final static int port = 10334;
    //delay-test
    private final static String regionName = "delay-test";
    private final static int loadConditionalInterval = 1000 * 30;
    private final static int maxConnections = 40;
    private final static int minConnections = 10;
    private final static int readTimeoutMs = 10*1000;
//    private final static int readTimeoutMs = 10;
    private final static int freeConnectionTimeout = 600000;
    private final static int threadCount = 40;

    @Test
    void timeout() throws InterruptedException {
        ClientCacheFactory cacheFactory = new ClientCacheFactory()
                .addPoolLocator(host, port);

        try (var cache = cacheFactory
                .setPoolLoadConditioningInterval(loadConditionalInterval)
//                .setPoolMaxConnections(maxConnections)
                .setPoolReadTimeout(readTimeoutMs)
                .setPoolMaxConnections(minConnections)
                .setPoolFreeConnectionTimeout(freeConnectionTimeout)
                .setPoolMaxConnectionsPerServer(maxConnections)
                .create()) {

                var region = cache.createClientRegionFactory(ClientRegionShortcut.PROXY)
                        .create(regionName);


                var boss = Boss.getBoss();

                Runnable runnable = () -> {
//                    int i = 0;
                    while (true) {
                        var start = System.currentTimeMillis();
                        try {
                            out.println("Putting value");
                            region.put("key", "value");
                            Thread.sleep(1000);
                            out.println("Putting value");
                        }
                        catch(AllConnectionsInUseException e){
                            out.println("GOT IT!!");
                        }
                        catch (Exception e) {
                            out.println("Time Seconds: " + ((System.currentTimeMillis() - start) / 1000) + " " + e);
                        }

                    }
                };

                Thread t = null;
                for (int i = 0; i < threadCount; i++) {
                    t = new Thread(runnable);
                    t.start();
                }

                t.join();


        }
    }
}
package com.vmware.data.services.gemfire.qa.performance;

import nyla.solutions.core.operations.performance.BenchMarker;
import nyla.solutions.core.patterns.creational.generator.json.JsonGeneratorCreator;
import nyla.solutions.core.util.stats.MathematicStats;
import nyla.solutions.core.util.stats.Mathematics;
import org.apache.geode.cache.Region;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;

import java.util.function.Consumer;

/**
 * @author Gregory Green
 */
public class PdxJsonPerf
{
    private final BenchMarker benchMarker;
    private final PdxJsonPutPerfRunner runner;
    private final Consumer<? extends Number> [] consumers;

    /*
    JsonGeneratorCreator jsonGeneratorCreator,
                                Converter<String, PdxInstance> converter,
                                Region<K,PdxInstance> region,
                                Function<PdxInstance,K > getIdFunc
     */
    public PdxJsonPerf(BenchMarker benchMarker,
                       JsonGeneratorCreator jsonGeneratorCreator,
                       Region<String, PdxInstance> region,
                       String idField, int capacity)
    {
        this(benchMarker,
                new PdxJsonPutPerfRunner(
                        jsonGeneratorCreator,
                        (json) -> JSONFormatter.fromJSON((String)json),
                        region,
                        new GetFromPdx(idField)),
                new MathematicStats(capacity, new Mathematics()));
    }
    public PdxJsonPerf(BenchMarker benchMarker, PdxJsonPutPerfRunner runner, Consumer<Number>... consumers)
    {
        this.benchMarker = benchMarker;
        this.runner = runner;
        this.consumers = consumers;
    }


    public void runPerTest()
    throws InterruptedException
    {
        benchMarker.measure(this.runner,consumers);
    }
}

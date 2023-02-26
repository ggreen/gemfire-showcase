package io.spring.gemfire.perftest.components;

import nyla.solutions.core.operations.performance.BenchMarker;
import nyla.solutions.core.operations.performance.PerformanceCheck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static nyla.solutions.core.util.Debugger.println;

@Component
public class StartPerfCmd implements CommandLineRunner {
    private final BenchMarker benchMarker;
    private final int capacity;
    private final Runnable runner;

    public StartPerfCmd(BenchMarker benchMarker,
                        @Value("${capacity}") int capacity,
                        Runnable runner) {

        this.benchMarker = benchMarker;
        this.capacity = capacity;
        this.runner = runner;
    }

    @Override
    public void run(String... args) throws Exception {
        var perfTest = new PerformanceCheck(benchMarker,capacity);
        perfTest.perfCheck(runner);

        println(this,"report:\n"+perfTest.getReport());
    }
}

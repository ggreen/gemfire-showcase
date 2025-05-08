package io.spring.gemfire.perftest.components;

import nyla.solutions.core.operations.performance.BenchMarker;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class StartPerfCmdTest {

    @Test
    void run() throws Exception {

        var marker = BenchMarker.builder()
                .loopCount(1L)
                .threadCount(1)
                .threadLifeTimeSeconds(10L)
                .build();

        var runner  = mock(Runnable.class);

        var subject = new StartPerfCmd(marker, 10000,runner,false);

        subject.run();

        verify(runner).run();
    }
}
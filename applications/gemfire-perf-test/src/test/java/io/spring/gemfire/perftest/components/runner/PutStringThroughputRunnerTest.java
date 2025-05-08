package io.spring.gemfire.perftest.components.runner;

import org.apache.geode.cache.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PutStringThroughputRunnerTest {

    @Mock
    private Region<String, String> region;

    private PutStringThroughputRunner subject;

    @BeforeEach
    void setUp() {
        subject = new PutStringThroughputRunner(10,"1",10,region);
    }

    @Test
    void run() {
        subject.run();
        verify(region).put(anyString(),anyString());
    }
}
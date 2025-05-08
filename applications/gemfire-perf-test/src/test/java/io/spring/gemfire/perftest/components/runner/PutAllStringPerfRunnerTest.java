package io.spring.gemfire.perftest.components.runner;

import org.apache.geode.cache.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PutAllStringPerfRunnerTest {

    private final int  valueLength = 25;
    private final int keyLength = 15;
    private final int putCount = 3;
    private final String seedText = "Hello";

    @Mock
    private Region<String, String> region ;

    @BeforeEach
    void setUp() {
    }

    @Test
    void run() {
        var subject = new PutAllStringPerfRunner(putCount, keyLength, valueLength,seedText,region);

        subject.run();

        assertThat(subject.getMap()).isNotNull();

        verify(region).putAll(any(Map.class));
    }

}

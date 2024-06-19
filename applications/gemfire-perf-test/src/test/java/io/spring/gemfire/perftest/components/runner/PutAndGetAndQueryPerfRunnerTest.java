package io.spring.gemfire.perftest.components.runner;

import com.vmware.data.services.gemfire.io.QuerierService;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PutAndGetAndQueryPerfRunnerTest {

    private PutAndGetAndQueryPerfRunner subject;

    @Mock
    private Region<Object, Object> region;
    @Mock
    private QuerierService queryService;
    private int batchSize = 1;
    private int keyPadLength = 1;
    private int valueLength =1;
    private String seedText = "TEXT";
    private String queryByKey ="select key from /test.entries where key = $1";

    @BeforeEach
    void setUp() {
        subject = new PutAndGetAndQueryPerfRunner(region,queryService,
                batchSize,
                keyPadLength,
                valueLength,
                seedText,
                queryByKey
                );
    }

    @Test
    void run() {
        subject.run();

        verify(region).get(any());
        verify(region).put(any(),any());
        verify(queryService).query(anyString(),anyString());
    }
}
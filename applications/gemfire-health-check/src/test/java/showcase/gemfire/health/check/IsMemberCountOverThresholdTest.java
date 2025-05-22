package showcase.gemfire.health.check;

import org.apache.geode.management.DistributedSystemMXBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IsMemberCountOverThresholdTest {

    private IsMemberCountOverThreshold subject;
    @Mock
    private DistributedSystemMXBean distributeSystem;
    private int minCacheServerCount = 2;

    @BeforeEach
    void setUp() {
        subject = new IsMemberCountOverThreshold(distributeSystem,minCacheServerCount);
    }

    @Test
    void over() {
        Integer memberCount = 3;
        when(distributeSystem.getMemberCount()).thenReturn(memberCount);

        Boolean expected = true;
        Boolean actual = subject.get();

        assertEquals(expected, actual);
    }


    @Test
    void under() {
        Integer memberCount = 1;
        when(distributeSystem.getMemberCount()).thenReturn(memberCount);

        Boolean expected = false;
        Boolean actual = subject.get();

        assertEquals(expected, actual);
    }
}
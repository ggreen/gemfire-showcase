package showcase.gemfire.health.runner;

import org.apache.geode.management.MemberMXBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import showcase.gemfire.health.check.IsMemberMemoryOverThreshold;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IsMemberMemoryOverThresholdTest {

    private IsMemberMemoryOverThreshold subject;

    @Mock
    private MBeanServer jmxConnection;

    @Mock
    private Function<ObjectName, MemberMXBean> getMemberBeanFunction;

    private double memoryThreshold = 50;

    @Mock
    private MemberMXBean memberMxBean;
    @Mock
    private ObjectName memberObjectName;

    @BeforeEach
    void setUp() {
        subject = new IsMemberMemoryOverThreshold(jmxConnection,
                getMemberBeanFunction,
                memoryThreshold);
    }

    @Test
    void requiredWithHighMemory() {

        Set<ObjectName> objectNames= Set.of(memberObjectName);
        when(jmxConnection.queryNames(any(), Mockito.isNull())).thenReturn(objectNames);
        when(getMemberBeanFunction.apply(any())).thenReturn(memberMxBean);
        Long maxMemory = 10L;
        Long usedMemory = 9L;
        when(memberMxBean.getMaxMemory()).thenReturn(maxMemory);
        when(memberMxBean.getUsedMemory()).thenReturn(usedMemory);



        var actual = subject.get();

        assertThat(actual).isTrue();
    }
}
package showcase.gemfire.health.runner;

import org.apache.geode.management.MemberMXBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import showcase.gemfire.health.check.IsAverageMemberUsedMemoryAboveThreshold;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IsAverageMemberUsedMemoryAboveThresholdTest {

    private IsAverageMemberUsedMemoryAboveThreshold subject;

    @Mock
    private MBeanServer jmxConnection;

    @Mock
    private Function<ObjectName, MemberMXBean> getMemberBeanFunction;

    private int memoryThreshold = 25;

    @Mock
    private MemberMXBean memberMxBean;
    @Mock
    private ObjectName memberObjectName;

    @BeforeEach
    void setUp() {
        subject = new IsAverageMemberUsedMemoryAboveThreshold(jmxConnection,
                getMemberBeanFunction,
                memoryThreshold);
    }

    @Test
    void requiredWithHighMemory() {

        Set<ObjectName> objectNames= Set.of(memberObjectName);
        when(jmxConnection.queryNames(any(), Mockito.isNull())).thenReturn(objectNames);
        when(getMemberBeanFunction.apply(any())).thenReturn(memberMxBean);
        Long usedMemory = 9L;
        when(memberMxBean.getUsedMemory()).thenReturn(usedMemory);
        var actual = subject.get();

        assertThat(actual).isFalse();
    }

    @Test
    void notRebalance() {
        assertThat(subject.isBalance(
                List.of(3l,8l,8l,100l))).isFalse();
    }

    @Test
    void isbalancedSingleValue() {

        assertThat(subject.isBalance(
                List.of(7l))).isTrue();
    }
    @Test
    void isbalanced() {

        assertThat(subject.isBalance(
                List.of(7l,8l,8l,10l))).isTrue();
    }
}
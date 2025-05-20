package showcase.gemfire.health.check.runner;

import org.apache.geode.management.MemberMXBean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import showcase.gemfire.health.check.rebalance.RebalanceCommand;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import java.util.Set;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoSafeRebalanceRunnerTest {
    private AutoSafeRebalanceRunner subject;
    @Mock
    private MBeanServerConnection jmxConnection;
    @Mock
    private RebalanceCommand rebalanceCommand;

    @Mock
    private MemberMXBean memberMxBean;

    @Mock
    private ObjectName memberObjectName;
    @Mock
    private Function<ObjectName, MemberMXBean> getMemberMxBean;

    @Mock
    private AutoSafeGemFireSupport autoSafeGemFireSupport;

    private double memoryThreshold = 50;
    private String[] args =null;

    @Test
    void autoRebalance() throws Exception {

        Set<ObjectName> objectNames= Set.of(memberObjectName);
        when(jmxConnection.queryNames(any(), Mockito.isNull())).thenReturn(objectNames);
        when(getMemberMxBean.apply(any())).thenReturn(memberMxBean);
        Long maxMemory = 10L;
        Long usedMemory = 7L;
        when(memberMxBean.getMaxMemory()).thenReturn(maxMemory);
        when(memberMxBean.getUsedMemory()).thenReturn(usedMemory);

        subject = new AutoSafeRebalanceRunner(jmxConnection,rebalanceCommand,memoryThreshold,0,getMemberMxBean,autoSafeGemFireSupport);

        subject.run(args);

        verify(rebalanceCommand).execute();
    }


    @Test
    void autoRebalance_notAboveThreshold() throws Exception {

        Set<ObjectName> objectNames= Set.of(memberObjectName);
        when(jmxConnection.queryNames(any(), Mockito.isNull())).thenReturn(objectNames);
        when(getMemberMxBean.apply(any())).thenReturn(memberMxBean);
        Long maxMemory = 10L;
        Long usedMemory = 1L;
        when(memberMxBean.getMaxMemory()).thenReturn(maxMemory);
        when(memberMxBean.getUsedMemory()).thenReturn(usedMemory);

        subject = new AutoSafeRebalanceRunner(jmxConnection,rebalanceCommand,memoryThreshold, 0,getMemberMxBean,autoSafeGemFireSupport);

        subject.run(args);

        verify(rebalanceCommand,never()).execute();
    }


    @DisplayName("Given high memory and member less than expect When run Then no run rebalance command")
    @Test
    void autoRebalance_lessThanExpectedMembers() throws Exception {

        Set<ObjectName> objectNames= Set.of(memberObjectName);
        when(jmxConnection.queryNames(any(), Mockito.isNull())).thenReturn(objectNames);
        when(getMemberMxBean.apply(any())).thenReturn(memberMxBean);
        Long maxMemory = 10L;
        Long usedMemory = 11L;
        when(memberMxBean.getMaxMemory()).thenReturn(maxMemory);
        when(memberMxBean.getUsedMemory()).thenReturn(usedMemory);
        when(autoSafeGemFireSupport.getServerCount()).thenReturn(1);
        int rebalanceMinServerCount = 2;

        subject = new AutoSafeRebalanceRunner(jmxConnection,rebalanceCommand,memoryThreshold, rebalanceMinServerCount,getMemberMxBean,autoSafeGemFireSupport);

        subject.run(args);

        verify(rebalanceCommand,never()).execute();
    }


}
package showcase.gemfire.health.check;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.management.MemberMXBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Check the cluster and execute a autoSafe as needed
 *
 * @author gregory green
 */
@Component("IsRebalanceRequired")
@Slf4j
public class IsRebalanceRequired implements Supplier<Boolean> {
    private final MBeanServerConnection jmxConnection;
    private final Function<ObjectName,MemberMXBean> getMemberBeanFunction;
    private final double memoryThreshold;

    public IsRebalanceRequired(MBeanServerConnection jmxConnection,
                               Function<ObjectName, MemberMXBean> getMemberBeanFunction,
                               @Value("${gemfire.threshold.memory.percentage}")
                                       double memoryThreshold) {
        this.jmxConnection = jmxConnection;
        this.getMemberBeanFunction = getMemberBeanFunction;
        this.memoryThreshold = memoryThreshold;
    }

    @SneakyThrows
    public Boolean get(){
        Set<ObjectName> members = jmxConnection.queryNames(new ObjectName("GemFire:type=Member,member=*"),
                null         );

        MemberMXBean memberBean;
        for (ObjectName member : members) {
            memberBean =  getMemberBeanFunction.apply(member);

            log.info("Checking member: {}",memberBean);

            var name = memberBean.getName();
            log.info("Member name: {}",name);

            var maxHeap = memberBean.getMaxMemory();
            var usedHeap = memberBean.getUsedMemory();

            double usagePercentage = ((double) usedHeap / maxHeap) * 100;

            log.info("Member: {}, Heap Usage: {}} ({}}dMB/{}}MB)", name, usagePercentage, usedHeap, maxHeap);

            if (usagePercentage > memoryThreshold) {
                log.info("  WARNING: Member {}} is above heap usage threshold!", name);

                return true;
            }
        }

        return false;
    }
}

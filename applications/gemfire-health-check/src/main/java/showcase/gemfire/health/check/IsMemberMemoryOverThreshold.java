package showcase.gemfire.health.check;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.management.MemberMXBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Check the cluster and execute a autoSafe as needed
 *
 * @author gregory green
 */
@Component("IsMemberMemoryOverThreshold")
@Slf4j
public class IsMemberMemoryOverThreshold implements Supplier<Boolean> {
    private final MBeanServer jmxConnection;
    private final Function<ObjectName,MemberMXBean> getMemberBeanFunction;
    private final double memoryThreshold;

    public IsMemberMemoryOverThreshold(MBeanServer jmxConnection,
                                       Function<ObjectName, MemberMXBean> getMemberBeanFunction,
                                       @Value("${gemfire.threshold.memory.percentage}")
                                       double memoryThreshold) {
        this.jmxConnection = jmxConnection;
        this.getMemberBeanFunction = getMemberBeanFunction;
        this.memoryThreshold = memoryThreshold;
    }

    @SneakyThrows
    public Boolean get(){
        Set<ObjectName> members = jmxConnection.queryNames(new ObjectName("GemFire:type=Member,*"), null);

        MemberMXBean memberBean;
        for (ObjectName member : members) {
            memberBean =  getMemberBeanFunction.apply(member);

            var name = memberBean.getName();
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

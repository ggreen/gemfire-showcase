package showcase.gemfire.health.check;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.management.MemberMXBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Check the cluster and execute a autoSafe as needed
 *
 * @author gregory green
 */
@Component("IsAverageMemberUsedMemoryAboveThreshold")
@Slf4j
public class IsAverageMemberUsedMemoryAboveThreshold implements Supplier<Boolean> {
    private final MBeanServerConnection jmxConnection;
    private final Function<ObjectName,MemberMXBean> getMemberBeanFunction;
    private final int memoryPercentageThreshold;

    public IsAverageMemberUsedMemoryAboveThreshold(MBeanServerConnection jmxConnection,
                                                   Function<ObjectName, MemberMXBean> getMemberBeanFunction,
                                                   @Value("${gemfire.check.threshold.member.memory.used.above.average:50}")
                                       int memoryPercentageThreshold) {
        this.jmxConnection = jmxConnection;
        this.getMemberBeanFunction = getMemberBeanFunction;
        this.memoryPercentageThreshold = memoryPercentageThreshold;
    }


    @SneakyThrows
    public Boolean get(){
        Set<ObjectName> members = jmxConnection.queryNames(new ObjectName("GemFire:type=Member,member=*"),
                null         );

        MemberMXBean memberBean;

        var serversUsedMemory = new ArrayList<Long>(members.size());
        for (ObjectName member : members) {
            memberBean =  getMemberBeanFunction.apply(member);
            if(memberBean.isLocator()){
                log.info("Skipping locator: ",member);
                continue;
            }
            serversUsedMemory.add(memberBean.getUsedMemory());
        }

        return !isBalance(serversUsedMemory);
    }

    public boolean isBalance(List<Long> serversUsedMemory) {

        log.info("Servers used memory: {}",serversUsedMemory);

        var average = serversUsedMemory.stream().mapToDouble(v -> v).average().orElse(0);

        for (Long usedMemory  : serversUsedMemory)
        {
            var differenceFromAverage = average - usedMemory;

            var diffPercentage = (differenceFromAverage/average)*100;
            log.info("usedMemory: {}, differenceFromAverage: {}, diffPercentage: {}",
                    usedMemory, differenceFromAverage,diffPercentage);

            if(diffPercentage > memoryPercentageThreshold)
                return false;
        }
        return true;
    }
}

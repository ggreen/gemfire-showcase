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
//@Component("IsMemberMemoryAboveThreshold")
@Slf4j
public class IsMemberMemoryAboveThreshold implements Supplier<Boolean> {
    private final MBeanServerConnection jmxConnection;
    private final Function<ObjectName,MemberMXBean> getMemberBeanFunction;
    private final int memoryPercentageThreshold;

    public IsMemberMemoryAboveThreshold(MBeanServerConnection jmxConnection,
                                        Function<ObjectName, MemberMXBean> getMemberBeanFunction,
                                        @Value("${}")
                                       int memoryPercentageThreshold) {
        this.jmxConnection = jmxConnection;
        this.getMemberBeanFunction = getMemberBeanFunction;
        this.memoryPercentageThreshold = memoryPercentageThreshold;
    }


    public List<Long> getValuesGreaterThanAverageByPercentage(List<Long> numbers, int percentage) {
        if (numbers == null || numbers.isEmpty()) {
            return null;
        }

        var average = numbers.stream().mapToLong(val-> val).average().orElse(0);
        log.info("Average memory usages is {}",average);

        var matchesMemoryOverPercentageThreshold = new ArrayList<Long>();
        for (Long usedMemory : numbers)
        {
            log.info("Memory usages: {}",usedMemory);

            //5 5 5 10
            var differenceFromAverage = average-usedMemory;
            log.info("differenceFromAverage: {} with avg:{}: note negatives mean use memory is above avergage",differenceFromAverage,average);
            var differencePercentage = (differenceFromAverage/average)*100;

            log.info("differencePercentage: {}",differencePercentage);
            if( differencePercentage >= percentage) //if greater than percentage
                    matchesMemoryOverPercentageThreshold.add(usedMemory);
        }

        return !matchesMemoryOverPercentageThreshold.isEmpty()? matchesMemoryOverPercentageThreshold : null;

    }


    @SneakyThrows
    public Boolean get(){
        Set<ObjectName> members = jmxConnection.queryNames(new ObjectName("GemFire:type=Member,member=*"),
                null         );

        MemberMXBean memberBean;

        var serversUsedMemory = new ArrayList<Long>(members.size());
        for (ObjectName member : members) {
            memberBean =  getMemberBeanFunction.apply(member);
            serversUsedMemory.add(memberBean.getUsedMemory());
        }

        return !isBalance(serversUsedMemory);
    }

    public boolean isBalance(List<Long> serversUsedMemory) {

        var average = serversUsedMemory.stream().mapToDouble(v -> v).average().orElse(0);

        for (Long usedMemory  : serversUsedMemory)
        {
            var differenceFromAverage = average - usedMemory;

            var diffPercentage = (differenceFromAverage/average)*100;
            log.info("usedMemory: {}, differenceFromAverage: {}, diffPercentage:",
                    usedMemory, differenceFromAverage,diffPercentage);

            if(diffPercentage > memoryPercentageThreshold)
                return false;
        }
        return true;
    }
}

package showcase.gemfire.health.check;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.management.RegionMXBean;
import org.springframework.stereotype.Component;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * AreBuckets UnBalanced there is at least one member with zero buckets
 * And other members have buckets.
 *
 * @author gregory green
 */
@Component("AreBucketsUnBalanced")
@Slf4j
public class AreBucketsUnBalanced implements Supplier<Boolean> {
    private final MBeanServerConnection jmxConnection;
    private final Function<ObjectName,RegionMXBean> getRegionMXBeanFunction;

    public AreBucketsUnBalanced(MBeanServerConnection jmxConnection,
                                Function<ObjectName, RegionMXBean> getRegionMXBeanFunction)
    {
        this.jmxConnection = jmxConnection;
        this.getRegionMXBeanFunction = getRegionMXBeanFunction;
    }

    @SneakyThrows
    public Boolean get(){
        Set<ObjectName> members = jmxConnection.queryNames(new ObjectName("GemFire:service=Region,name=*,type=Member,member=*"),
                null         );

        RegionMXBean regionMxBean;
        Map<String, List<Integer>> regionBuckets = new HashMap<>();
        for (ObjectName member : members) {
            regionMxBean =  getRegionMXBeanFunction.apply(member);

            log.info("Checking region: {}",regionMxBean);

            var name = regionMxBean.getName();
            var memberBucketCounts = regionMxBean.getBucketCount();

            log.info("region name: {}, member: {}, memberBucketCounts: {}",name,regionMxBean.getMember(), memberBucketCounts);

            if(regionBuckets.containsKey(name))
                regionBuckets.get(name).add(memberBucketCounts);
            else
                regionBuckets.put(name, new ArrayList<>(List.of(memberBucketCounts)));
        }

        //Check if any regions have
        for (Map.Entry<String,List<Integer>> entry : regionBuckets.entrySet())
        {
            var listBuckets = entry.getValue();
            var regionName = entry.getKey();
            log.info("Checking regions: {}, buckets: {}",regionName,listBuckets);
            if(listBuckets.contains(0) && listBuckets.stream().anyMatch(v -> v > 0))
            {
                log.info("Region: {}, is unbalanced",regionName);
                return true;
            }
        }

        return false;
    }
}

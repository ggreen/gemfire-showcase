package showcase.gemfire.health.check;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.management.RegionMXBean;
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
public class HasNumBucketsWithoutRedundancy implements Supplier<Boolean> {
    private final MBeanServerConnection jmxConnection;
    private final Function<ObjectName,RegionMXBean> getRegionMXBeanFunction;

    public HasNumBucketsWithoutRedundancy(MBeanServerConnection jmxConnection,
                                          Function<ObjectName, RegionMXBean> getRegionMXBeanFunction)
    {
        this.jmxConnection = jmxConnection;
        this.getRegionMXBeanFunction = getRegionMXBeanFunction;
    }

    @SneakyThrows
    public Boolean get(){
        Set<ObjectName> members = jmxConnection.queryNames(new ObjectName("GemFire:service=Region,name=*,type=Distributed"),
                null         );

        RegionMXBean regionMxBean;
        for (ObjectName member : members) {
            regionMxBean =  getRegionMXBeanFunction.apply(member);

            log.info("Checking region: {}",regionMxBean);

            var name = regionMxBean.getName();
            log.info("region name: {}",name);

            if(regionMxBean.getNumBucketsWithoutRedundancy() > 0)
                return true;

        }

        return false;
    }
}

package showcase.gemfire.health.check;

import org.apache.geode.management.RegionMXBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.io.IOException;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AreBucketsUnBalancedTest {


    private AreBucketsUnBalanced subject;

    @Mock
    private MBeanServerConnection jmxConnection;

    @Mock
    private Set<ObjectName> regions;

    @Mock
    private ObjectName objectName1,objectName2;

    @Mock
    private Function<ObjectName, RegionMXBean> getRegionMxBeanFunction;

    @Mock
    private RegionMXBean regionMXBean;


    @BeforeEach
    void setUp() {
        regions = Set.of(objectName1,objectName2);

        subject = new AreBucketsUnBalanced(jmxConnection,getRegionMxBeanFunction);
    }

    @Test
    void isTrueIfHasBucketCountZero() throws IOException {

        when(jmxConnection.queryNames(any(ObjectName.class), Mockito.isNull())).thenReturn(regions);
        when(getRegionMxBeanFunction.apply(any())).thenReturn(regionMXBean);
        when(regionMXBean.getBucketCount()).thenReturn(50)
                .thenReturn(0);
        assertThat(subject.get()).isTrue();
    }


    @Test
    void isFalseIfBucketCountEquals() throws IOException {

        when(jmxConnection.queryNames(any(ObjectName.class), Mockito.isNull())).thenReturn(regions);
        when(getRegionMxBeanFunction.apply(any())).thenReturn(regionMXBean);
        when(regionMXBean.getBucketCount()).thenReturn(50)
                .thenReturn(50);
        assertThat(subject.get()).isFalse();
    }
}
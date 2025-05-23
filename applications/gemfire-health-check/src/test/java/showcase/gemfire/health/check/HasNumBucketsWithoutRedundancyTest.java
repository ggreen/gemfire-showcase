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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HasNumBucketsWithoutRedundancyTest {

    private HasNumBucketsWithoutRedundancy subject;

    @Mock
    private Function<ObjectName, RegionMXBean> getRegionMxBeanFunction;

    @Mock
    private MBeanServerConnection jmxConnection;

    @Mock
    private ObjectName objectName;

    @Mock
    private RegionMXBean regionMXBean;

    private Set<ObjectName> regions;

    @BeforeEach
    void setUp() {
       regions = Set.of(objectName);

        subject = new HasNumBucketsWithoutRedundancy(jmxConnection,getRegionMxBeanFunction);
    }

    @Test
    void isFalseIfWithoutRedundancy() throws IOException {

        when(jmxConnection.queryNames(any(ObjectName.class), Mockito.isNull())).thenReturn(regions);
        when(getRegionMxBeanFunction.apply(any())).thenReturn(regionMXBean);
        when(regionMXBean.getNumBucketsWithoutRedundancy()).thenReturn(0);
        var actual = subject.get();
        assertThat(actual).isFalse();
    }

    @Test
    void isFalseNoRegions() throws IOException {

        var actual = subject.get();
        assertThat(actual).isFalse();
    }


        @Test
    void isTrueIfWithoutRedundancy() throws IOException {


        when(jmxConnection.queryNames(any(ObjectName.class), Mockito.isNull())).thenReturn(regions);
        when(getRegionMxBeanFunction.apply(any())).thenReturn(regionMXBean);
        when(regionMXBean.getNumBucketsWithoutRedundancy()).thenReturn(1);
        var actual = subject.get();
        assertThat(actual).isTrue();

    }
}
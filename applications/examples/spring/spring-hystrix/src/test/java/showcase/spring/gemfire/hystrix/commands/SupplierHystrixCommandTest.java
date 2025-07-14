package showcase.spring.gemfire.hystrix.commands;

import org.apache.geode.cache.Region;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierHystrixCommandTest {

    private SupplierHystrixCommand<String> subject;

    @Mock
    private Region<String,String> region;
    private int timeout = 2;
    private int coreCount = 2;

    @Test
    void getRegionData() throws Exception {

        String key = "world";
        String expected = "hello";
        when(region.get(any())).thenReturn(expected);
        subject = new SupplierHystrixCommand<String>(
                ()-> region.get(key),
                () -> "FAILBACK",
                timeout,
                coreCount);

        var actual = subject.run();
        assertThat(actual).isEqualTo(expected);
    }
}
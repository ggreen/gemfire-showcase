package showcase.gemfire.health.fix;

import org.apache.geode.management.MemberMXBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JmxGemFireRebalanceCommandTest {

    @Mock
    private MemberMXBean locatorBean;

    @Mock
    private Supplier<Boolean> memberCountOverThreshold;
    private JmxGemFireRebalanceCommand subject;

    @BeforeEach
    void setUp() {
        subject = new JmxGemFireRebalanceCommand(memberCountOverThreshold,locatorBean);
    }

    @Test
    void execute() {

        when(memberCountOverThreshold.get()).thenReturn(true);

        subject.execute();

        verify(locatorBean).processCommand(anyString());
    }

    @Test
    void no_execute() {

        subject.execute();

        verify(memberCountOverThreshold).get();
        verify(locatorBean,never()).processCommand(anyString(),any(Map.class));
    }
}
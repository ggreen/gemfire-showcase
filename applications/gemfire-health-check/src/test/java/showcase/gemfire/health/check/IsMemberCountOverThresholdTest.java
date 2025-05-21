package showcase.gemfire.health.check;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IsMemberCountOverThresholdTest {

    private IsMemberCountOverThreshold subject;

    @BeforeEach
    void setUp() {
        subject = new IsMemberCountOverThreshold();
    }

    @Test
    void over() {
        Boolean expected = true;
        Boolean actual = subject.get();

        assertEquals(expected, actual);
    }
}
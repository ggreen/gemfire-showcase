package showcase.gemfire.health.check;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Check condition when rebalance is required
 * @author gregory green
 */
@ExtendWith(MockitoExtension.class)
class isRebalanceRequiredTest {

    private IsRebalanceRequired subject;
    @Mock
    private HasNumBucketsWithoutRedundancy redundancyCheck;
    @Mock
    private IsAverageMemberUsedMemoryAboveThreshold memoryCheck;


    @BeforeEach
    void setUp() {
        subject = new IsRebalanceRequired(redundancyCheck,memoryCheck);
    }

    @Test
    void trueIsMemoryAbove() {
        when (memoryCheck.get()).thenReturn(true);

        assertThat(subject.get()).isTrue();
    }

    @Test
    void trueIsWhenAboveToLoseData() {
        when (redundancyCheck.get()).thenReturn(true);

        assertThat(subject.get()).isTrue();
    }

    @Test
    void falseWhenEverythingOk() {

        assertThat(subject.get()).isFalse();
    }
}
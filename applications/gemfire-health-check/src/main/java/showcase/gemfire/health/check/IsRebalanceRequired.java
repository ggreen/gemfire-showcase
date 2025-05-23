package showcase.gemfire.health.check;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;


/**
 * Determines is a rebalance is required
 * @author gregory green
 */
@Component("IsRebalanceRequired")
@RequiredArgsConstructor
public class IsRebalanceRequired implements Supplier<Boolean> {

    private final HasNumBucketsWithoutRedundancy hasNumBucketsWithoutRedundancy;
    private final IsAverageMemberUsedMemoryAboveThreshold isAverageMemberUsedMemoryAboveThreshold;
    private final AreBucketsUnBalanced areBucketsUnBalanced;

    @Override
    public Boolean get() {
        return hasNumBucketsWithoutRedundancy.get() ||
                isAverageMemberUsedMemoryAboveThreshold.get() ||
                areBucketsUnBalanced.get();
    }
}

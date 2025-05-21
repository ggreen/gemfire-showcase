package showcase.gemfire.health.check;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component("IsMemberCountOverThreshold")
public class IsMemberCountOverThreshold implements Supplier<Boolean> {
    @Override
    public Boolean get() {
        return null;
    }
}

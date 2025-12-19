package showcase.gemfire.demo.functions.locking.demo;

import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.distributed.DistributedMember;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JunitResultConnection implements ResultCollector{
    private List<Object> results = new ArrayList<>();
    @Override
    public Object getResult() throws FunctionException {
        return null;
    }

    @Override
    public Object getResult(long l, TimeUnit timeUnit) throws FunctionException, InterruptedException {
        return getResult();
    }

    @Override
    public void addResult(DistributedMember distributedMember, Object o) {
        results.add(o);
    }

    @Override
    public void endResults() {
    }

    @Override
    public void clearResults() {

    }
}

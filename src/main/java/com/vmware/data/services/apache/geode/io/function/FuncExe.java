package com.vmware.data.services.apache.geode.io.function;

import com.vmware.data.services.apache.geode.io.GemFireIO;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;

import java.util.Collection;
import java.util.Set;

/**
 * Function execution
 * @author Gregory
 */
public class FuncExe
{
    private Execution execution;

    public FuncExe(Execution<?, ?, ?> execution)
    {
        this.execution = (Execution)execution;
    }

    public static FuncExe construct(Execution<?,?,?> execution)
    {
        return new FuncExe(execution);
    }

    public static <K, V> FuncExe onRegion(Region<K,V> region)
    {
        return new FuncExe(FunctionService.onRegion(region));
    }

    public <T> Collection<T> exe(Function<?> function) throws Exception
    {
        return GemFireIO.exeWithResults(this.execution,function);
    }

    public Execution<?,?,?> getExecution()
    {
        return execution;
    }

    public FuncExe withFilter(Set<?> filterSet)
    {
        this.execution.withFilter(filterSet);
        return this;
    }

    public FuncExe withCollector(ResultCollector<?, ?> collector)
    {
        execution = execution.withCollector((ResultCollector)collector);
        return this;
    }

    public <T> FuncExe setArguments(T args)
    {
        execution = execution.setArguments(args);
        return this;
    }
}

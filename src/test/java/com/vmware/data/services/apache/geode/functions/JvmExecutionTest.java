package com.vmware.data.services.apache.geode.functions;

import com.vmware.data.services.apache.geode.functions.JvmExecution;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

import java.util.Set;

/**
 * @author Gregory Green testing JvmExecution
 */
public class JvmExecutionTest
{

    @SuppressWarnings("unchecked")
    @Test
    public void test_withFilter()
    {

        Region<Object, Object> region = Mockito.mock(Region.class);
        JvmExecution<?, ?, ?> jvm = new JvmExecution<Object, Object, Object>(region);

        Set<?> set = Mockito.mock(Set.class);
        Execution<?, ?, ?> exe = jvm.withFilter(set);

        assertEquals(jvm, exe);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_withArgs()
    {

        Region<Object, Object> region = Mockito.mock(Region.class);
        JvmExecution<?, ?, ?> jvm = new JvmExecution<Object, Object, Object>(region);

        Execution<?, ?, ?> exe = jvm.withArgs("");

        assertEquals(jvm, exe);

    }
}

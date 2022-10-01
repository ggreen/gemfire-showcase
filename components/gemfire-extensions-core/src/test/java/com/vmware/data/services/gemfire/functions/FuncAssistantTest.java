package com.vmware.data.services.gemfire.functions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test for FuncAssistant
 *
 * @author Gregory Green
 */

public class FuncAssistantTest
{
    @Test
    public void test_getLocalPrimaryData()
    {
        Region<?, ?> region = Mockito.mock(Region.class);

        RegionFunctionContext rfc = Mockito.mock(JvmRegionFunctionContext.class);

        Region<?, ?> localPrimaryData = FuncAssistant.getLocalPrimaryData(region, rfc);

        assertNotNull(localPrimaryData);

    }
}

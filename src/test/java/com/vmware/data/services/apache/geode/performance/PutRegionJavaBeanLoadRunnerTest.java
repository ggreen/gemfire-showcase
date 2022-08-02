package com.vmware.data.services.apache.geode.performance;

import com.vmware.data.services.apache.geode.qa.performance.PutRegionJavaBeanLoadRunner;
import nyla.solutions.core.security.user.data.UserProfile;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class PutRegionJavaBeanLoadRunnerTest
{

    @SuppressWarnings("unchecked")
    @Test
    public void test()
    {
        Region<String, UserProfile> region = mock(Region.class);

        int count = 1;

        Runnable runner = new PutRegionJavaBeanLoadRunner<UserProfile>(count, region, UserProfile.class, 1);

        runner.run();

        verify(region).put(anyString(), any());


    }

}

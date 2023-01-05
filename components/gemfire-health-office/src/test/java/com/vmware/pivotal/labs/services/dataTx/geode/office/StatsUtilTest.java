package com.vmware.pivotal.labs.services.dataTx.geode.office;

import com.vmware.data.services.gemfire.operations.stats.statInfo.ResourceInst;
import com.vmware.data.services.gemfire.operations.stats.statInfo.ResourceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;


public class StatsUtilTest
{
	@Test
	public void testAppName()
	{
		assertNull(StatsUtil.getAppName(null));
		
		ResourceInst resourceInst = mock(ResourceInst.class);
		when(resourceInst.getName()).thenReturn("server1");
		
		ResourceType rt = mock(ResourceType.class);
		
		
		when(resourceInst.getType()).thenReturn(rt);
		when(rt.getName()).thenReturn(StatsConstants.CACHE_SERVER_STAT_NM);
		
		ResourceInst[] resources = {resourceInst};
		
		
		assertEquals("server1",StatsUtil.getAppName(resources));
		
		
	}
	
	@Test
	public void testFormatMachine()
	{
		assertEquals("server", StatsUtil.formatMachine("server"));
	}

}

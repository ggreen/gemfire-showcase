package io.spring.gemfire.rest.app;

import static org.mockito.Mockito.*;

import com.vmware.data.services.gemfire.client.GemFireClient;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class GemFireRegionRestServiceTest
{
	GemFireClient gemfire;
	Region<Object,Object> geodeRegion;
	GemFireRegionRestService subject;
	String region = "test";

	@BeforeEach
	public void setUp()
	{
		gemfire = mock(GemFireClient.class);
		geodeRegion = mock(Region.class);
		when(gemfire.getRegion(anyString())).thenReturn(geodeRegion);

		PdxService pdxService = mock(PdxService.class);
		subject = new GemFireRegionRestService(pdxService);
		subject.gemfire = gemfire;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPut()
	throws Exception
	{
		String key = "key";
		String value = "{\"@type\" : \"package\"}";


		subject.putEntry(region, key, value);
		verify(geodeRegion).put(any(),any());
	}

	@Test
	public void test_get()
	{
		String key = "expected";
		String type = "type";
		String results = subject.getValueByKey(region, key, type);
		verify(geodeRegion).get(any());
	}

	@Test
	public void testRemove()
	throws Exception
	{
		String region = null;
		String key = null;
		String value = null;

		region = "test";
		subject.delete(region, key);
		verify(geodeRegion).remove(any());
	}

}

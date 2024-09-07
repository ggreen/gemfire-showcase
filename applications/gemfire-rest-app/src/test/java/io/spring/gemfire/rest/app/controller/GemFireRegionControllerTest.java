package io.spring.gemfire.rest.app.controller;

import static org.mockito.Mockito.*;

import com.vmware.data.services.gemfire.client.GemFireClient;
import io.spring.gemfire.rest.app.exception.FaultAgent;
import io.spring.gemfire.rest.app.service.PdxService;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class GemFireRegionControllerTest
{
	private GemFireRegionController subject;

	@Mock
	private GemFireClient gemFireClient;

	@Mock
	private Region<Object,Object> gemFireRegion;

	@Mock
	private FaultAgent faultAgent;
	@Mock
	private PdxService pdxService;

	private String region = "test";

	@BeforeEach
	public void setUp()
	{
		when(gemFireClient.getRegion(anyString())).thenReturn(gemFireRegion);

		subject = new GemFireRegionController(gemFireClient, pdxService,faultAgent);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPut()
	throws Exception
	{
		String key = "key";
		String value = "{\"@type\" : \"package\"}";

		when(gemFireClient.getRegion(anyString())).thenReturn(gemFireRegion);
		subject.putEntry(region, key, value);
		verify(gemFireRegion).put(any(),any());
	}

	@Test
	public void test_get()
	{
		String key = "expected";
		String type = "type";
		String results = subject.getValueByKey(region, key, type);
		verify(gemFireRegion).get(any());
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
		verify(gemFireRegion).remove(any());
	}

}

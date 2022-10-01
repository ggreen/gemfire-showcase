package com.vmware.data.services.gemfire.listeners;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Consumer;

import com.vmware.data.services.gemfire.client.listeners.CacheListenerBridge;
import org.apache.geode.cache.EntryEvent;
import org.junit.jupiter.api.*;

public class CacheListenerBridgeTest
{

	private static boolean testAfterPutFlag = false;
	private static boolean testAfterDeleteFlag = false;
	@Test
	public void testAfterPut()
	{
		Consumer<EntryEvent<String, Object>> c = e -> testAfterPutFlag = true;
		CacheListenerBridge<String, Object> bridge = CacheListenerBridge.forAfterPut(c);
		
		EntryEvent<String, Object> event = null;
		
		bridge.afterCreate(event);
		assertTrue(testAfterPutFlag);
		
		testAfterPutFlag = false;
		bridge.afterUpdate(event);
		assertTrue(testAfterPutFlag);
		
	}

	@Test
	public void testAfterRemoved()
	{
		Consumer<EntryEvent<String, Object>> c = e -> testAfterDeleteFlag = true;
		CacheListenerBridge<String, Object> bridge = CacheListenerBridge.forAfterDelete(c);
		
		EntryEvent<String, Object> event = null;
		
		bridge.afterDestroy(event);
		assertTrue(testAfterDeleteFlag);
		
		testAfterDeleteFlag = false;
		bridge.afterInvalidate(event);
		assertTrue(testAfterDeleteFlag);

		
	}


}

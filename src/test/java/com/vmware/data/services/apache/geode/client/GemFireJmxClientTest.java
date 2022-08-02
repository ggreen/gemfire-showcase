package com.vmware.data.services.apache.geode.client;



import static org.junit.jupiter.api.Assertions.*;

import com.vmware.data.services.apache.geode.client.GemFireJmxClient;
import org.junit.jupiter.api.*;


import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import nyla.solutions.core.patterns.jmx.JMX;
import nyla.solutions.core.util.Debugger;



//@Disabled
/**
 *
 * locator-idp-606068407.us-west-2.elb.amazonaws.com
 * cache-idp-104800883.us-west-2.elb.amazonaws.com
 * cache2-idp-1239450408.us-west-2.elb.amazonaws.com
 *
 * @author Gregory Green
 *
 */
@Disabled
public class GemFireJmxClientTest
{
	static ClientCacheFactory  factory = null;
	static ClientCache cache  = null;
	static Region<Object, Object> region= null;

	//@BeforeAll
	public static void setUp()
	{

		//factory = new ClientCacheFactory().addPoolLocator("ec2-52-27-12-106.us-west-2.compute.amazonaws.com", 10000)
		//locator2-idp-979146816.us-west-2.elb.amazonaws.com
		factory = new ClientCacheFactory().addPoolLocator("locator2-idp-979146816.us-west-2.elb.amazonaws.com", 10000)
				.set("log-level", "fine").set("log-file", "target/client.log");
		cache = factory.create();

		region = cache
		.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY).create("Test");
	}


	//@Test
	public void Lookup()
	{
		String host = GemFireJmxClient.lookupNetworkHost("localhost");
		Debugger.println("host:"+host);
		Assertions.assertNotNull(host);
	}

	//@Test
	public void put()
	{

	    region.put("world", "hello");

		Assertions.assertEquals("hello",region.get("world"));

		System.out.println(region.get("world"));
	}

	//@Test
	public void get()
	{

		region = cache.getRegion("Test");
		Assertions.assertEquals("hello",region.get("world"));

		System.out.println(region.get("world"));
	}

	//@Test
	public void getRegion()
	{
		//52.54.153.210 11099
		JMX jmx = JMX.connect("54.69.23.36", 11099);
		//54.198.131.183

		//JMX jmx = JMX.connect("54.198.105.20", 11099);

		//Address, UserProfile
		Region<String,String> region = GemFireJmxClient.getRegion("Inventories", jmx);

		region.put("greg", "hello");

		Assertions.assertEquals("hello",region.get("greg"));
	}

}

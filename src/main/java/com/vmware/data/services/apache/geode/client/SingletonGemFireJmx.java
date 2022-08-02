package com.vmware.data.services.apache.geode.client;

import java.io.File;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

import nyla.solutions.core.exception.SystemException;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.patterns.jmx.JMX;
import nyla.solutions.core.util.Config;


/**
 * This class contains a set of utility operations to support QA testing
 * @author Gregory Green
 *
 */
public class SingletonGemFireJmx
{
	private static JMX jmx = null;

	/**
	 * 
	 * @param directory the directory the clear
	 * @param pattern the folder name match pattern
	 */
	public static void clearDirectory(File directory, String pattern)
	{
		
		File[] files = IO.listFiles(directory, pattern);
		
		if(files == null || files.length == 0)
			return;
		
		for (File file : files)
		{
			if(!file.delete())
				throw new SystemException("cannot delete:"+file);
				
		}
	}// --------------------------------------------------------
	/**
	 * Close and recreate the JMX connect
	 * @return the JMX connection
	 */
	public synchronized static JMX reconnect()
	{
		try
		{

				ClientCache cache = null;
				
				cache = ClientCacheFactory.getAnyInstance();
					
				if(cache != null && !cache.isClosed())
				{
						cache.close();
				}
		}
		catch (Exception e)
		{
				System.out.println("Cache was closed");
		}
		
		if(jmx != null)
		{
			
			jmx.dispose();
			jmx = null;
		}
					
		return getJmx();
	}// --------------------------------------------------------
	/**
	 * Connect/Reconnect to a locator host/port
	 * @param locatorHost the locator host
	 * @param locatorPort the locator port
	 * @return the JMX connection
	 */
	public static synchronized JMX reconnectJMX(String locatorHost, int locatorPort)
	{
			
		try
		{

				ClientCache cache = null;
				
				cache = ClientCacheFactory.getAnyInstance();
					
				if(cache != null && !cache.isClosed())
				{
						cache.close();
				}
				
				
		}
		catch (Exception e)
		{
				System.out.println("Cache was closed");
		}
		
		if(jmx != null)
		{
			
			jmx.dispose();
			jmx = null;
		}
			
		SingletonGemFireJmx.setLocatorJmxHost(locatorHost);
		SingletonGemFireJmx.setLocatorPort(locatorPort);
			
		return getJmx();
	}// --------------------------------------------------------
	
	public synchronized static JMX getJmx()
	{
		if(jmx == null)
			jmx = JMX.connect(locatorJmxHost,locatorJmxPort);
		
		return jmx;
	}
	
	
	public static void dispose()
	{
		if(jmx != null) try{ jmx.dispose(); } catch(Exception e){e.printStackTrace();}
		jmx = null;
	}
	
		
	/**
	 * @return the locatorHost
	 */
	public static String getLocatorHost()
	{
		return locatorJmxHost;
	}


	/**
	 * @param locatorJmxHost the locatorHost to set
	 */
	public static void setLocatorJmxHost(String locatorJmxHost)
	{
		SingletonGemFireJmx.locatorJmxHost = locatorJmxHost;
	}


	/**
	 * @return the locatorJmxPort
	 */
	public static int getLocatorJmxPort()
	{
		return locatorJmxPort;
	}


	/**
	 * @param locatorPort the locatorPort to set
	 */
	public static void setLocatorPort(int locatorPort)
	{
		SingletonGemFireJmx.locatorJmxPort = locatorPort;
	}

	private static String locatorJmxHost = Config.getProperty(SingletonGemFireJmx.class,"locatorJmxHost");
	private static int locatorJmxPort = Config.getPropertyInteger(SingletonGemFireJmx.class,"locatorJmxPort").intValue();
}

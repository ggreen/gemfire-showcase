package com.vmware.data.services.apache.geode.util;

import nyla.solutions.core.net.Networking;

/**
 * Utility class for various common GF related functionality
 * 
 * @author Gregory Green
 *
 */
public final class GemFireNetworking
{
	/**
	 * Parse the remote locators and locator and assert that they match
	 * @param remoteLocators the remote locator
	 * @param locators the  local locator
	 * @return true if locators match
	 */
	public static boolean checkRemoteLocatorsAndLocatorsMatch(String remoteLocators, String locators)
	{
		
		if(remoteLocators == null || remoteLocators.length() == 0)
			return false;
		
		if(remoteLocators.equalsIgnoreCase(locators))
			return true;
			
		String[] remoteLocatorsArray = remoteLocators.split(",");
				
		if(locators == null || locators.length() == 0)
			return false;
		
		String[] locatorsArray = locators.split(",");
		
		String remoteLocatorHost, locatorHost;
		int remoteLocatorPort, locatorPort;

			for (String remoteLocator : remoteLocatorsArray)
			{
				if(remoteLocator == null || remoteLocator.length() == 0)
					continue;
				
				//parse host
				for(String locator: locatorsArray)
				{
					if(locator == null || locator.length() == 0)
						continue;
					
					try
					{
						remoteLocatorHost = parseLocatorHost(remoteLocator);
						locatorHost = parseLocatorHost(locator);
						remoteLocatorPort = parseLocatorPort(remoteLocator);
						locatorPort = parseLocatorPort(locator);
						
						if(Networking.hostEquals(remoteLocatorHost,locatorHost)
							&& remoteLocatorPort == locatorPort)
						{
							return true;
						}
						else
						{
							//check if ip address match
							

						}
					}
					catch (NumberFormatException e)
					{
						//port parse exception
						return false;
					}
					catch (IllegalArgumentException e)
					{
						throw new IllegalArgumentException("remoteLocator:"+remoteLocator+" locator:"+locator+" ERROR:"+e.getMessage(),e);
					}
				}
			}
			
			return false;

	}// --------------------------------------------------------
	private static String parseLocatorHost(String locator)
	{
		
		int i = locator.indexOf("[");
		
		if(i > 0)
			return locator.substring(0,i).trim();
		else
			return locator.trim();
	}// --------------------------------------------------------
	private static int parseLocatorPort(String locator)
	{
		int start = locator.indexOf("[");
		
		String text = null;
		if(start > 0)
		{
			String results = locator.substring(start+1);
			
			int end = results.indexOf("]");
			
			if(end > 0)
			{
				text = results.substring(0,end).trim();
				return Integer.parseInt(text);
			}
		}
		
		throw new IllegalArgumentException("Expected format host[port] but provided with:"+locator);
				
	}// -------------------------------------------------------- 
	


}

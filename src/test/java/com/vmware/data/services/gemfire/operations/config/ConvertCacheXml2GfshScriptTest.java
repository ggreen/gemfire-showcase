package com.vmware.data.services.gemfire.operations.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

public class ConvertCacheXml2GfshScriptTest
{

	@Test
	public void testNoArgs() throws Exception
	{
		String[] args = {};
		
		try 
		{
			ConvertCacheXml2GfshScript.main(args);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			
		}
	}
	
	@Test
	public void testMain()
	{
		String[] args = {"src/test/resources/xml/clusterCopy.xml"};
		
		ConvertCacheXml2GfshScript.main(args);
	}

}

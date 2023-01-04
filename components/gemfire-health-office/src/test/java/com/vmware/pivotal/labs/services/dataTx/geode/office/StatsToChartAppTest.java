package com.vmware.pivotal.labs.services.dataTx.geode.office;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for simple App.
 */
public class StatsToChartAppTest
{
	@Test
    public void testApp()
    {
    	String [] args = {};
    	try
    	{ 
	    	StatsToChartApp.main(args);
	    	fail("NA");
    	}
    	catch(IllegalArgumentException e)
    	{}
  
    }
}

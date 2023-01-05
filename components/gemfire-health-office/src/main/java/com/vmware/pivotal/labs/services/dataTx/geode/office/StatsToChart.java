package com.vmware.pivotal.labs.services.dataTx.geode.office;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.vmware.data.services.gemfire.operations.stats.GfStatsReader;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.patterns.conversion.Converter;
import nyla.solutions.office.chart.Chart;


/**
 * <pre>
 * Converts the statistics in files/directory into a Chart.
 * 
 * See CpuAboveThresholdChartStatsVisitor
 * 
 * The initial chart implementation uses JFreeChart
 * </pre>
 * 
 * @author Gregory Green
 * 
 */
public class StatsToChart implements Converter<File, Chart>
{
	

	public StatsToChart(ChartStatsVisitor visitor)
	{
		this.visitor = visitor;
	}


	/**
	 * Accepts a file or directory contain the statistics files
	 * @param file the file or directory
	 * @return the chart for files
	 */
	public Chart convert(File file)
	{
		if(file == null)
			return null;
		
		try
		{
			
			
		    if(file.isDirectory())
		    {
		    	//Process for all files
		    	Set<File> statsFiles = IO.listFileRecursive(file, "*.gfs");
		    	if(statsFiles == null || statsFiles.isEmpty())
		    		return null;
		    	
		    	for (File statFile : statsFiles)
				{
		    		GfStatsReader reader = new GfStatsReader(statFile.getAbsolutePath());
			    	
			    	reader.acceptVisitors(visitor);
				}
		    }
		    else
		    {
		    	GfStatsReader reader = new GfStatsReader(file.getAbsolutePath());
		    	
		    	reader.acceptVisitors(visitor);
		    	
		    }
			
			return visitor.getChart();
		}
		catch (IOException e)
		{
			throw new RuntimeException ("File:"+file+" ERROR:"+e.getMessage(),e);
		}
	}

	
	private final ChartStatsVisitor visitor;
}

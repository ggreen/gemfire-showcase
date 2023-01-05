package com.vmware.pivotal.labs.services.dataTx.geode.office;

import java.io.File;
import java.io.IOException;

import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.io.IO;
import nyla.solutions.office.chart.Chart;

/**
 * <pre>
 * Usages 
 * 
 * java io.pivotal.services.dataTx.geode.office.StatsToChartApp  <i>fileOrDirectoryWithStats</i> <i>pngFilePath</i>
 * </pre>
 */
public class StatsToChartApp 
{
    public static void main( String[] args )
    {
    	if(args.length != 3)
    	{
			System.out.println("Usage java "+StatsToChart.class.getName()+" <fileOrDirectoryWithStats> <outputDirectory> <filterDay");
			System.out.flush();
    		System.exit(0);
    	}
    	
    	
    	try
		{
				
	    	Day dataFilter = new Day(args[2]);
	    	
			StatsToChart cpuChartConvert = new StatsToChart(new CpuAboveThresholdChartStatsVisitor(dataFilter));
			File inputFileOrDirectory = new File(args[0]);

			File cpuFilePath = new File(args[1]+"/cpu.png");
			File parNewCollectionTimesFilePath = new File(args[1]+"/parNewCollectionTimes.png");
			File parNewCollectionsFilePath = new File(args[1]+"/parNewCollections.png");
			File jvmMemoryFilePath = new File(args[1]+"/jvmMemory.png");
			
			Chart cpuChart = cpuChartConvert.convert(inputFileOrDirectory);
			IO.writeFile(cpuFilePath, cpuChart.getBytes());
			
			
			Chart parNewChart = new StatsToChart
			(new ParNewCollectionTimeThresholdChartStatsVisitor(dataFilter))
			.convert(inputFileOrDirectory);

			IO.writeFile(parNewCollectionTimesFilePath, parNewChart.getBytes());
			
			Chart parNewCollections = new StatsToChart(new YoungGenerationGcChartStatsVisitor(dataFilter))
			.convert(inputFileOrDirectory);

			IO.writeFile(parNewCollectionsFilePath, parNewCollections.getBytes());
			
			
			Chart jvmMemoryChart = new StatsToChart(new JvmMaxHeapUsageAboveThresholdChartStatsVisitor())
			.convert(inputFileOrDirectory);

			IO.writeFile(jvmMemoryFilePath, jvmMemoryChart.getBytes());
			
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
    }//------------------------------------------------
}

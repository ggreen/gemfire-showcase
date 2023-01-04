package com.vmware.pivotal.labs.services.dataTx.geode.office;


import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceInst;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceType;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.StatValue;
import nyla.solutions.office.chart.Chart;

/**
 * <pre>
 * Generate charts for the Jvm Avg Heap Usage Above a given Threshold.
 * </pre>
 * @author Gregory Green
 *
 */
public class JvmAvgHeapUsageAboveThresholdChartStatsVisitor extends AbstractChartVisitor
{
	private  int percentThreshold = 50;

	/**
	 * Constructor sets the chart and percent threshold
	 *
	 */
	public JvmAvgHeapUsageAboveThresholdChartStatsVisitor()
	{
		this.chart.setGraphType(Chart.AREA_GRAPH_TYPE);

		this.setPercentThreshold(percentThreshold);

	}

	public int getPercentThreshold()
	{
		return percentThreshold;
	}

	public void setPercentThreshold(int percentThreshold) {
		this.percentThreshold = percentThreshold;

		String title = "JVM AVG memory greater than "+this.percentThreshold+"% ";

		this.chart.setTitle(title);
	}

	@Override
	public void visitResourceInst(ResourceInst resourceInst)
	{
		String name = resourceInst.getName();
		if("vmNonHeapMemoryStats".equals(name))
			return; //skip;


		String machineName = resourceInst.getArchive().getArchiveInfo().getMachine();
		
		ResourceType resourceType= resourceInst.getType();
		final  String filterTypeName = "VMMemoryUsageStats";
		
		boolean skip =  resourceType == null || resourceType.getName() == null || 
		(filterTypeName != null && !resourceType.getName().equals(filterTypeName));
		
		if(skip)
		{
			//System.out.println("skipping resourceType:"+resourceType+" name:"+name);
			return;
		}

		
	
		StatValue[] statValues = resourceInst.getStatValues();
		if(statValues == null)
			return;
		
			
		StatValue usedMemoryStatValue = resourceInst.getStatValue("usedMemory");	
		
		double usedMemory = usedMemoryStatValue.getSnapshotsAverage()/ BYTES_GB;
		
	    StatValue maxMemoryStatValue = resourceInst.getStatValue("maxMemory");	
		
		double maxMemory = maxMemoryStatValue.getSnapshotsMaximum()/(BYTES_GB);
		
		
		double percent = usedMemory/maxMemory * 100;
		
		
		if(percent < percentThreshold)
			return;
		
		this.chart.plotValue(maxMemory, "available", machineName);
		this.chart.plotValue(usedMemory, "used", machineName);
	
	}

}

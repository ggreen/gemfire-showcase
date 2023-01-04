package com.vmware.pivotal.labs.services.dataTx.geode.office;



import com.vmware.data.services.apache.geode.operations.stats.GfStatsReader;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.ArchiveInfo;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceInst;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceType;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.StatValue;
import nyla.solutions.office.chart.Chart;

/**
 * <pre>
 * Generates a chart of the maximum Heap Usage Above a given Threshold
 * </pre>
 * @author Gregory Green
 *
 */
public class JvmMaxHeapUsageAboveThresholdChartStatsVisitor extends AbstractChartVisitor
{
	private   int percentThreshold;
	/**
	 * DEFAULT_MAX_THRESHOLD = 50
	 */
	public static final int DEFAULT_MAX_THRESHOLD = 50;
	/**
	 * Default constructor
	 */
	public JvmMaxHeapUsageAboveThresholdChartStatsVisitor()
	{
		this(DEFAULT_MAX_THRESHOLD);//default Max 50 usages
	}//------------------------------------------------v
	public JvmMaxHeapUsageAboveThresholdChartStatsVisitor(int percentThreshold)
	{
		this.percentThreshold  = percentThreshold;
		
		this.setPercentThreshold(this.percentThreshold);

		this.chart.setGraphType(Chart.AREA_GRAPH_TYPE);

	}

	

	@Override
	public void visitResourceInst(ResourceInst resourceInst)
	{
		if(resourceInst == null)
			return;
		
		String name = resourceInst.getName();
		if("vmNonHeapMemoryStats".equals(name))
			return; //skip;
		
		GfStatsReader reader= resourceInst.getArchive();
		
		if(reader == null)
			return;
		
		ArchiveInfo ai = reader.getArchiveInfo();
		
		if(ai == null)
			return;
		
		String machineName = ai.getMachine();
		
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
		
		double usedMemory = usedMemoryStatValue.getSnapshotsMaximum()/(BYTES_GB);
		
	    StatValue maxMemoryStatValue = resourceInst.getStatValue("maxMemory");	
		
		double maxMemory = maxMemoryStatValue.getSnapshotsMaximum()/(BYTES_GB);
		
		
		double percent = usedMemory/maxMemory * 100;
		
		
		if(percent < percentThreshold)
			return;
		
		this.chart.plotValue(maxMemory, "available", machineName);
		this.chart.plotValue(usedMemory, "used", machineName);
	
		
		
	}//------------------------------------------------
	public void setPercentThreshold(int percentThreshold) {
		this.percentThreshold = percentThreshold;

		String title = "JVM MAX memory greater than " + this.percentThreshold + "% ";

		this.chart.setTitle(title);
	}
}

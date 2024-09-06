package com.vmware.pivotal.labs.services.dataTx.geode.office;


import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.vmware.pivotal.labs.services.dataTx.geode.office.stats.statInfo.ResourceInst;
import com.vmware.pivotal.labs.services.dataTx.geode.office.stats.statInfo.ResourceType;
import com.vmware.pivotal.labs.services.dataTx.geode.office.stats.statInfo.StatDescriptor;
import com.vmware.pivotal.labs.services.dataTx.geode.office.stats.statInfo.StatValue;
import nyla.solutions.core.data.Property;
import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.util.Text;
import nyla.solutions.office.chart.Chart;

/**
 * <pre>
 * Generate chart for Cpu Above a given Threshold.
 * 
 * </pre>
 * @author Gregory Green
 *
 */
public class CpuAboveThresholdChartStatsVisitor extends AbstractChartVisitor
{
	private Map<Property,Double> maxMap = new TreeMap<Property,Double>();
	private String filterTypeName = "LinuxSystemStats".toUpperCase();
	private String filterStatName = "cpuActive";
	private final double cpuPercentThreshold;
	private final Day dayFilter;

	public CpuAboveThresholdChartStatsVisitor(Day dayFilter)
	{
		this(dayFilter,50.1);

	}
	public CpuAboveThresholdChartStatsVisitor(Day dayFilter,double cpuPercentThreshold)
	{
		this.cpuPercentThreshold = cpuPercentThreshold;
		this.dayFilter = dayFilter;
		
		String title = "CPU per minute over "+cpuPercentThreshold+" usage "+
		Text.formatDate("MM/dd/yyyy",dayFilter.getDate());

		this.chart.setTitle(title);

		this.chart.setGraphType(Chart.BAR_GRAPH_TYPE);
		this.chart.setHeight(1000);
		this.chart.setWidth(7000);
		//this.chart.setCategoryLabel(this.filterStatName);
	}//------------------------------------------------

//	@Override
	public void visitResourceInst(ResourceInst resourceInst)
	{
		String name = resourceInst.getName();
		
		String machine = resourceInst.getArchive().getArchiveInfo().getMachine();
		
		ResourceType resourceType= resourceInst.getType();
		
		boolean skip =  resourceType == null || resourceType.getName() == null || 
		(this.filterTypeName != null && !resourceType.getName().toUpperCase().contains(this.filterTypeName));
		
		if(skip)
		{
			return;
		}
	
		StatValue[] statValues = resourceInst.getStatValues();
		if(statValues == null)
			return;
		

			
			StatValue dataStoreEntryCount = resourceInst.getStatValue(filterStatName);

			if(dataStoreEntryCount.getSnapshotsMaximum() < cpuPercentThreshold)
				return;

			StatDescriptor statDescriptor = dataStoreEntryCount.getDescriptor();


			long [] times = dataStoreEntryCount.getRawAbsoluteTimeStamps();
			double [] values = dataStoreEntryCount.getSnapshots();
			
			
			String timeFormat = "HH:mm";
			
			
			for (int i = 0; i < values.length; i++)
			{
				Date date = new Date(times[i]);
			
				Day day = new Day(date);
				if(!this.dayFilter.isSameDay(day))
					continue;
					
				String timeValueText = Text.formatDate(timeFormat,date);
				Property timeValue = new Property(name,timeValueText);
				
				//get previous Max
				Double max = this.maxMap.get(timeValue);
				
				if(max == null)
					max = Double.valueOf(values[i]);
				else
					max = Double.valueOf(Math.max(values[i], max.doubleValue()));
				
				this.maxMap.put(timeValue, max);
				
				if(values[i] >= cpuPercentThreshold)
					this.chart.plotValue(max,machine, (String)timeValue.getValue());
			}

	}
}
